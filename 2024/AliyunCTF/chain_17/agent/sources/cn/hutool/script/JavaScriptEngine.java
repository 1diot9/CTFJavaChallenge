package cn.hutool.script;

import java.io.Reader;
import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/script/JavaScriptEngine.class */
public class JavaScriptEngine extends FullSupportScriptEngine {
    public JavaScriptEngine() {
        super(ScriptUtil.createJsEngine());
    }

    public static JavaScriptEngine instance() {
        return new JavaScriptEngine();
    }

    @Override // cn.hutool.script.FullSupportScriptEngine
    public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
        return this.engine.invokeMethod(thiz, name, args);
    }

    @Override // cn.hutool.script.FullSupportScriptEngine
    public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
        return this.engine.invokeFunction(name, args);
    }

    @Override // cn.hutool.script.FullSupportScriptEngine
    public <T> T getInterface(Class<T> cls) {
        return (T) this.engine.getInterface(cls);
    }

    @Override // cn.hutool.script.FullSupportScriptEngine
    public <T> T getInterface(Object obj, Class<T> cls) {
        return (T) this.engine.getInterface(obj, cls);
    }

    @Override // cn.hutool.script.FullSupportScriptEngine
    public CompiledScript compile(String script) throws ScriptException {
        return this.engine.compile(script);
    }

    @Override // cn.hutool.script.FullSupportScriptEngine
    public CompiledScript compile(Reader script) throws ScriptException {
        return this.engine.compile(script);
    }

    @Override // cn.hutool.script.FullSupportScriptEngine
    public Object eval(String script, ScriptContext context) throws ScriptException {
        return this.engine.eval(script, context);
    }

    @Override // cn.hutool.script.FullSupportScriptEngine
    public Object eval(Reader reader, ScriptContext context) throws ScriptException {
        return this.engine.eval(reader, context);
    }

    @Override // cn.hutool.script.FullSupportScriptEngine
    public Object eval(String script) throws ScriptException {
        return this.engine.eval(script);
    }

    @Override // cn.hutool.script.FullSupportScriptEngine
    public Object eval(Reader reader) throws ScriptException {
        return this.engine.eval(reader);
    }

    @Override // cn.hutool.script.FullSupportScriptEngine
    public Object eval(String script, Bindings n) throws ScriptException {
        return this.engine.eval(script, n);
    }

    @Override // cn.hutool.script.FullSupportScriptEngine
    public Object eval(Reader reader, Bindings n) throws ScriptException {
        return this.engine.eval(reader, n);
    }

    @Override // cn.hutool.script.FullSupportScriptEngine
    public void put(String key, Object value) {
        this.engine.put(key, value);
    }

    @Override // cn.hutool.script.FullSupportScriptEngine
    public Object get(String key) {
        return this.engine.get(key);
    }

    @Override // cn.hutool.script.FullSupportScriptEngine
    public Bindings getBindings(int scope) {
        return this.engine.getBindings(scope);
    }

    @Override // cn.hutool.script.FullSupportScriptEngine
    public void setBindings(Bindings bindings, int scope) {
        this.engine.setBindings(bindings, scope);
    }

    @Override // cn.hutool.script.FullSupportScriptEngine
    public Bindings createBindings() {
        return this.engine.createBindings();
    }

    @Override // cn.hutool.script.FullSupportScriptEngine
    public ScriptContext getContext() {
        return this.engine.getContext();
    }

    @Override // cn.hutool.script.FullSupportScriptEngine
    public void setContext(ScriptContext context) {
        this.engine.setContext(context);
    }

    @Override // cn.hutool.script.FullSupportScriptEngine
    public ScriptEngineFactory getFactory() {
        return this.engine.getFactory();
    }
}
