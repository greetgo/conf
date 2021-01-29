### Features

#### Lazy config file creation OR annotation @ForcibleInit

Look two fragments of code:

```
/* Line 1 */ SomeConfig config = configFactory.createConfig(SomeConfig.class);
```

```
/* Line 2 */ config.someParameter();
```

Line 1 creates implementation of config interface.

Line 2 reads some parameter from config file at the first time after line 1.

If config file is absent, then the system creates config file at line 2, not at line 1.

If you want the system creates config file at line 1, then you need mark the config interface by annotation

    kz.greetgo.conf.hot.ForcibleInit

#### Enum as parameter
