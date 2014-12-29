package kz.greepto.gpen.views.gpen;

import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import kz.greepto.gpen.editors.gpen.GpenSelection;
import kz.greepto.gpen.editors.gpen.prop.PropAccessor;
import kz.greepto.gpen.editors.gpen.prop.PropList;
import kz.greepto.gpen.editors.gpen.prop.PropOptions;
import kz.greepto.gpen.util.Handler;
import kz.greepto.gpen.util.HandlerKiller;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class GpenPropertyView extends ViewPart {
  private Composite parent;
  
  private Composite forFocus;
  
  private ISelectionListener listener;
  
  private final List<HandlerKiller> killers = new LinkedList<HandlerKiller>();
  
  public void killAll() {
    final Procedure1<HandlerKiller> _function = new Procedure1<HandlerKiller>() {
      public void apply(final HandlerKiller it) {
        it.kill();
      }
    };
    IterableExtensions.<HandlerKiller>forEach(this.killers, _function);
    this.killers.clear();
  }
  
  public void setFocus() {
    boolean _notEquals = (!Objects.equal(this.forFocus, null));
    if (_notEquals) {
      this.forFocus.setFocus();
    }
  }
  
  public void createPartControl(final Composite parent) {
    this.parent = parent;
    final ISelectionListener _function = new ISelectionListener() {
      public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
        if ((selection instanceof GpenSelection)) {
          GpenPropertyView.this.setSelection(((GpenSelection) selection));
        } else {
          GpenPropertyView.this.setSelection(null);
        }
      }
    };
    this.listener = _function;
    IWorkbenchPartSite _site = this.getSite();
    IWorkbenchWindow _workbenchWindow = _site.getWorkbenchWindow();
    ISelectionService _selectionService = _workbenchWindow.getSelectionService();
    _selectionService.addSelectionListener(this.listener);
  }
  
  public void dispose() {
    boolean _notEquals = (!Objects.equal(this.listener, null));
    if (_notEquals) {
      IWorkbenchPartSite _site = this.getSite();
      IWorkbenchWindow _workbenchWindow = _site.getWorkbenchWindow();
      ISelectionService _selectionService = _workbenchWindow.getSelectionService();
      _selectionService.removeSelectionListener(this.listener);
      this.listener = null;
    }
    this.killAll();
    super.dispose();
  }
  
  public void setSelection(final GpenSelection sel) {
    boolean _isDisposed = this.parent.isDisposed();
    if (_isDisposed) {
      return;
    }
    Control[] _children = this.parent.getChildren();
    final Procedure1<Control> _function = new Procedure1<Control>() {
      public void apply(final Control it) {
        it.dispose();
      }
    };
    IterableExtensions.<Control>forEach(((Iterable<Control>)Conversions.doWrapArray(_children)), _function);
    this.killAll();
    boolean _or = false;
    boolean _tripleEquals = (sel == null);
    if (_tripleEquals) {
      _or = true;
    } else {
      int _size = sel.ids.size();
      boolean _equals = (_size == 0);
      _or = _equals;
    }
    if (_or) {
      Label lab = new Label(this.parent, SWT.NONE);
      lab.setText("Выделите элементы в Gpen Editor-е");
      this.parent.layout(true);
      return;
    }
    ScrolledComposite sc = new ScrolledComposite(this.parent, (SWT.V_SCROLL + SWT.H_SCROLL));
    Composite wall = new Composite(sc, (SWT.NONE + SWT.BORDER));
    sc.setContent(wall);
    sc.setExpandHorizontal(true);
    sc.setExpandVertical(true);
    GridLayout lay = new GridLayout();
    lay.numColumns = 3;
    wall.setLayout(lay);
    PropList _propList = sel.getPropList();
    for (final PropAccessor prop : _propList) {
      this.appendPropWidgets(wall, prop);
    }
    {
      Label lab_1 = new Label(wall, SWT.NONE);
      lab_1.setText("");
    }
    Label _label = new Label(wall, SWT.NONE);
    _label.setText("");
    {
      final Label lab_1 = new Label(wall, SWT.NONE);
      lab_1.setText("                                             ");
    }
    Point _computeSize = wall.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    sc.setMinSize(_computeSize);
    this.parent.layout(true);
  }
  
  public void appendPropWidgets(final Composite wall, final PropAccessor prop) {
    PropOptions _options = prop.getOptions();
    boolean _isReadonly = _options.isReadonly();
    if (_isReadonly) {
      this.appendReadonlyWidget(wall, prop);
      return;
    }
    boolean _or = false;
    Class<?> _type = prop.getType();
    boolean _equals = Objects.equal(_type, Integer.class);
    if (_equals) {
      _or = true;
    } else {
      Class<?> _type_1 = prop.getType();
      boolean _equals_1 = Objects.equal(_type_1, Integer.TYPE);
      _or = _equals_1;
    }
    if (_or) {
      this.appendIntWidget(wall, prop);
      return;
    }
    Class<?> _type_2 = prop.getType();
    boolean _equals_2 = Objects.equal(_type_2, String.class);
    if (_equals_2) {
      this.appendStrWidget(wall, prop);
      return;
    }
    boolean _or_1 = false;
    Class<?> _type_3 = prop.getType();
    boolean _equals_3 = Objects.equal(_type_3, Boolean.class);
    if (_equals_3) {
      _or_1 = true;
    } else {
      Class<?> _type_4 = prop.getType();
      boolean _equals_4 = Objects.equal(_type_4, Boolean.TYPE);
      _or_1 = _equals_4;
    }
    if (_or_1) {
      this.appendBoolWidget(wall, prop);
      return;
    }
  }
  
  public boolean appendReadonlyWidget(final Composite wall, final PropAccessor prop) {
    boolean _xblockexpression = false;
    {
      {
        Label lab = new Label(wall, SWT.NONE);
        String _name = prop.getName();
        lab.setText(_name);
      }
      Label _label = new Label(wall, SWT.NONE);
      _label.setText(":");
      boolean _xblockexpression_1 = false;
      {
        final Label lab = new Label(wall, SWT.NONE);
        String _extractStr = this.extractStr(prop);
        lab.setText(_extractStr);
        final Handler _function = new Handler() {
          public void handle() {
            String _extractStr = GpenPropertyView.this.extractStr(prop);
            lab.setText(_extractStr);
          }
        };
        HandlerKiller _addChangeHandler = prop.addChangeHandler(_function);
        _xblockexpression_1 = this.killers.add(_addChangeHandler);
      }
      _xblockexpression = _xblockexpression_1;
    }
    return _xblockexpression;
  }
  
  public String extractStr(final PropAccessor prop) {
    Object value = prop.getValue();
    boolean _equals = Objects.equal(value, null);
    if (_equals) {
      return "";
    }
    if ((value instanceof Class<?>)) {
      Class<?> klass = ((Class<?>) value);
      return klass.getSimpleName();
    }
    if ((value instanceof String)) {
      return ((String) value);
    }
    return value.toString();
  }
  
  public boolean appendIntWidget(final Composite wall, final PropAccessor prop) {
    boolean _xblockexpression = false;
    {
      {
        Label lab = new Label(wall, SWT.NONE);
        String _name = prop.getName();
        lab.setText(_name);
      }
      Label _label = new Label(wall, SWT.NONE);
      _label.setText(":");
      boolean _xblockexpression_1 = false;
      {
        final Text txt = new Text(wall, (SWT.SINGLE + SWT.BORDER));
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        txt.setLayoutData(gd);
        final ArrayList<String> saved = new ArrayList<String>();
        String _extractStr = this.extractStr(prop);
        txt.setText(_extractStr);
        String _text = txt.getText();
        saved.add(_text);
        final ModifyListener _function = new ModifyListener() {
          public void modifyText(final ModifyEvent it) {
            String _text = txt.getText();
            String _get = saved.get(0);
            boolean _notEquals = (!Objects.equal(_text, _get));
            if (_notEquals) {
              try {
                String _text_1 = txt.getText();
                Integer _valueOf = Integer.valueOf(_text_1);
                prop.setValue(_valueOf);
              } catch (final Throwable _t) {
                if (_t instanceof NumberFormatException) {
                  final NumberFormatException e = (NumberFormatException)_t;
                  prop.setValue(Integer.valueOf(0));
                } else {
                  throw Exceptions.sneakyThrow(_t);
                }
              }
            }
          }
        };
        txt.addModifyListener(_function);
        final Handler _function_1 = new Handler() {
          public void handle() {
            String _extractStr = GpenPropertyView.this.extractStr(prop);
            txt.setText(_extractStr);
            String _text = txt.getText();
            saved.set(0, _text);
          }
        };
        HandlerKiller _addChangeHandler = prop.addChangeHandler(_function_1);
        _xblockexpression_1 = this.killers.add(_addChangeHandler);
      }
      _xblockexpression = _xblockexpression_1;
    }
    return _xblockexpression;
  }
  
  public boolean appendStrWidget(final Composite wall, final PropAccessor prop) {
    boolean _xblockexpression = false;
    {
      {
        Label lab = new Label(wall, SWT.NONE);
        String _name = prop.getName();
        lab.setText(_name);
      }
      Label _label = new Label(wall, SWT.NONE);
      _label.setText(":");
      boolean _xblockexpression_1 = false;
      {
        final Text txt = new Text(wall, (SWT.SINGLE + SWT.BORDER));
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        txt.setLayoutData(gd);
        final ArrayList<String> saved = new ArrayList<String>();
        String _extractStr = this.extractStr(prop);
        txt.setText(_extractStr);
        String _text = txt.getText();
        saved.add(_text);
        final ModifyListener _function = new ModifyListener() {
          public void modifyText(final ModifyEvent it) {
            String _get = saved.get(0);
            String _text = txt.getText();
            boolean _notEquals = (!Objects.equal(_get, _text));
            if (_notEquals) {
              String _text_1 = txt.getText();
              prop.setValue(_text_1);
            }
          }
        };
        txt.addModifyListener(_function);
        final Handler _function_1 = new Handler() {
          public void handle() {
            String _extractStr = GpenPropertyView.this.extractStr(prop);
            txt.setText(_extractStr);
            String _text = txt.getText();
            saved.set(0, _text);
          }
        };
        HandlerKiller _addChangeHandler = prop.addChangeHandler(_function_1);
        _xblockexpression_1 = this.killers.add(_addChangeHandler);
      }
      _xblockexpression = _xblockexpression_1;
    }
    return _xblockexpression;
  }
  
  public void appendBoolWidget(final Composite wall, final PropAccessor prop) {
    final Button btn = new Button(wall, SWT.CHECK);
    String _name = prop.getName();
    btn.setText(_name);
    GridData gd = new GridData();
    gd.horizontalSpan = 3;
    gd.horizontalAlignment = SWT.FILL;
    btn.setLayoutData(gd);
    Object _value = prop.getValue();
    if ((_value instanceof Boolean)) {
      btn.setGrayed(false);
      Object _value_1 = prop.getValue();
      btn.setSelection((((Boolean) _value_1)).booleanValue());
    } else {
      btn.setSelection(true);
      btn.setGrayed(true);
    }
    btn.addSelectionListener(
      new SelectionAdapter() {
        public void widgetSelected(final SelectionEvent e) {
          boolean _selection = btn.getSelection();
          prop.setValue(Boolean.valueOf(_selection));
          btn.setGrayed(false);
        }
      });
    final Handler _function = new Handler() {
      public void handle() {
        Object _value = prop.getValue();
        btn.setSelection((((Boolean) _value)).booleanValue());
      }
    };
    HandlerKiller _addChangeHandler = prop.addChangeHandler(_function);
    this.killers.add(_addChangeHandler);
  }
}
