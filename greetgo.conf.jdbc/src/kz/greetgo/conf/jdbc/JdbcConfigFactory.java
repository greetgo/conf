package kz.greetgo.conf.jdbc;

import kz.greetgo.conf.core.ConfAccess;
import kz.greetgo.conf.core.ConfContent;
import kz.greetgo.conf.core.ConfRecord;
import kz.greetgo.conf.hot.AbstractHotConfFactory;
import kz.greetgo.conf.jdbc.dialects.DbRegister;
import kz.greetgo.conf.jdbc.errors.NoSchema;
import kz.greetgo.conf.jdbc.errors.NoTable;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class JdbcConfigFactory extends AbstractHotConfFactory {

  protected NamingStyle namingStyle() {
    return NamingStyle.DEFAULT_DIALECT;
  }

  protected abstract DataSource dataSource();

  protected String schema() {
    return null;
  }

  protected String tablePrefix() {
    return null;
  }

  protected String tablePostfix() {
    return null;
  }

  public FieldNames fieldNames() {
    return new FieldNames() {
      @Override
      public String paramPath() {
        return "param_path";
      }

      @Override
      public String paramValue() {
        return "param_value";
      }

      @Override
      public String description() {
        return "description";
      }

      @Override
      public String modifiedAt() {
        return "modified_at";
      }
    };
  }

  protected String tableNameFor(Class<?> configInterface) {
    String tablePrefix   = tablePrefix();
    String tablePostfix  = tablePostfix();
    String interfaceName = extractInterfaceName(configInterface);
    return ""
             + (tablePrefix == null ? "" : tablePrefix)
             + interfaceName
             + (tablePostfix == null ? "" : tablePostfix)
      ;
  }

  private final AtomicReference<DbRegister> dbRegister = new AtomicReference<>(null);

  public final DbRegister register() {
    {
      DbRegister register = this.dbRegister.get();
      if (register != null) return register;
    }
    synchronized (dbRegister) {
      {
        DbRegister register = this.dbRegister.get();
        if (register != null) return register;
      }
      {
        DbRegister register = DbRegister.create(dataSource(), namingStyle());
        dbRegister.set(register);
        return register;
      }
    }
  }

  @Override
  public void reset() {
    super.reset();
    dbRegister.set(null);
  }

  @Override
  protected <T> ConfAccess confAccess(Class<T> configInterface) {
    return new ConfAccess() {
      @Override
      public ConfContent load() {
        String tableName = tableNameFor(configInterface);

        ConfContent ret = new ConfContent();

        try {
          ConfRecord tableDescriptionRecord = register().selectTableDescriptionRecord(schema(), tableName);
          if (tableDescriptionRecord != null && tableDescriptionRecord.comments.size() > 0) {
            ret.records.add(tableDescriptionRecord);
          }

          ret.records.addAll(register().selectParamRecords(schema(), tableName, fieldNames()));

          return ret.records.isEmpty() ? null : ret;
        } catch (NoSchema | NoTable e) {
          return null;
        }
      }

      void boom(Handler handler) {
        while (true) {
          try {
            handler.handle();
            return;
          } catch (NoSchema e) {
            register().createSchema(schema());
            continue;
          } catch (NoTable e) {
            register().createTable(schema(), tableNameFor(configInterface), fieldNames());
            continue;
          }
        }
      }

      <W> W doom(Supplier<W> supplier) {
        while (true) {
          try {
            return supplier.get();
          } catch (NoSchema e) {
            register().createSchema(schema());
            continue;
          } catch (NoTable e) {
            register().createTable(schema(), tableNameFor(configInterface), fieldNames());
            continue;
          }
        }
      }

      @Override
      public void write(ConfContent confContent) {
        String tableName = tableNameFor(configInterface);

        List<String> tableComments = confContent
                                       .records
                                       .stream()
                                       .filter(x -> x.key() == null)
                                       .flatMap(x -> x.comments.stream())
                                       .collect(Collectors.toList());

        boom(() -> register().setTableComments(schema(), tableName, fieldNames(), tableComments));

        Map<String, ConfRecord> existsRecords
          = doom(() -> register().selectParamRecords(schema(), tableName, fieldNames()))
              .stream()
              .filter(x -> x.key() != null && x.key().length() > 0)
              .collect(Collectors.toMap(ConfRecord::key, x -> x));

        for (ConfRecord record : confContent.records) {
          String paramPath = record.key();

          if (paramPath == null || paramPath.length() == 0) {
            continue;
          }

          existsRecords.remove(paramPath);

          boom(() -> register().upsertRecord(schema(), tableName, fieldNames(), record));
        }

        for (String paramPath : existsRecords.keySet()) {
          boom(() -> register().removeRecord(schema(), tableName, fieldNames(), paramPath));
        }
      }

      @Override
      public Date lastModifiedAt() {
        String tableName = tableNameFor(configInterface);
        try {
          return register().selectLastModifiedAt(schema(), tableName, fieldNames());
        } catch (NoSchema | NoTable e) {
          return null;
        }
      }
    };
  }

}
