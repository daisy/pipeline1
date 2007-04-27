package org.daisy.dmfc.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.daisy.dmfc.core.event.CoreMessageEvent;
import org.daisy.dmfc.core.event.EventBus;
import org.daisy.dmfc.core.event.JobStateChangeEvent;
import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.dmfc.core.event.StateChangeEvent;
import org.daisy.dmfc.core.event.TaskMessageEvent;
import org.daisy.dmfc.core.event.TaskProgressChangeEvent;
import org.daisy.dmfc.core.event.TaskStateChangeEvent;
import org.daisy.dmfc.core.script.Job;
import org.daisy.dmfc.core.script.Script;
import org.daisy.dmfc.core.script.ScriptParameter;
import org.daisy.dmfc.core.script.ScriptValidationException;
import org.daisy.dmfc.core.script.Task;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.dmfc.core.transformer.TransformerInfo;
import org.daisy.dmfc.exception.DMFCConfigurationException;
import org.daisy.dmfc.exception.ScriptException;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.pipeline.gui.PipelineGuiPlugin;
import org.daisy.pipeline.gui.jobs.model.JobManager;
import org.daisy.pipeline.gui.messages.MessageManager;
import org.daisy.pipeline.gui.scripts.ScriptManager;
import org.daisy.util.execution.State;
import org.daisy.util.file.EFolder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * @author Romain Deltour
 * 
 */
public class FakeCore {
    Bundle coreBundle;

    public FakeCore(InputListener inListener) throws DMFCConfigurationException {
        coreBundle = Platform.getBundle(PipelineGuiPlugin.CORE_ID);
        loadProperties();

    }

    public void execute(Job job) throws ScriptException {
        EventBus.getInstance().publish(
                new JobStateChangeEvent(job, StateChangeEvent.Status.STARTED));
        setState(job, State.RUNNING);
        for (Task task : job.getScript().getTasks()) {
            Transformer trans = new FakeTrans(task.getName());
            EventBus.getInstance().publish(
                    new TaskStateChangeEvent(task,
                            StateChangeEvent.Status.STARTED));
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                EventBus.getInstance().publish(
                        new TaskProgressChangeEvent(task,
                                ((double) i) / 100));
            }
            // EventBus.getInstance().publish(
            // new TaskProgressChangeEvent(trans, 1.0));
            EventBus.getInstance().publish(
                    new TaskStateChangeEvent(task,
                            StateChangeEvent.Status.STOPPED));

            // boolean success = handler.run(parameters,
            // task.isInteractive());
            // if (!success) {
            // job.setState(State.FAILED);
            // throw new ScriptException(i18n("TASK_FAILED",
            // handler.getName()));
            // }

        }
        setState(job, State.FINISHED);

        EventBus.getInstance().publish(
                new JobStateChangeEvent(job, StateChangeEvent.Status.STOPPED));

        // } catch (TransformerAbortException e) {
        // setState(job, State.ABORTED);
        // throw new ScriptAbortException("Task aborted", e);
        // } catch (TransformerRunException e) {
        // setState(job, State.FAILED);
        // throw new ScriptException("Task failed", e);
    }

    private void setState(Job job, State state) {
        try {
            Method setState = Job.class.getDeclaredMethod("setState",
                    new Class[] { State.class });
            setState.setAccessible(true);
            setState.invoke(job, new Object[] { state });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Script newScript(URL url) throws ScriptValidationException {
        Script script = null;
        try {
            Class parserClass = coreBundle
                    .loadClass("org.daisy.dmfc.core.script.Parser");
            Method getInstance = parserClass.getMethod("getInstance",
                    new Class[0]);
            getInstance.setAccessible(true);
            Object parser = getInstance.invoke(null, new Object[0]);
            Method newScript = parserClass.getMethod("newScript",
                    new Class[] { URL.class });
            newScript.setAccessible(true);
            script = (Script) newScript.invoke(parser, new Object[] { url });
        } catch (Exception e) {
            throw new ScriptValidationException(e.getCause().getMessage(), e
                    .getCause());
        }
        inspectScript(script);
        return script;
    }

    /**
     * @param script
     */
    private void inspectScript(Script script) {
        System.out.println("Created Script: " + script.getName());
        System.out.println(" - nice name: " + script.getNicename());
        System.out.println(" - descr: " + script.getDescription());
        System.out.println(" - doc: " + script.getDocumentation().toString());
        System.out.println(" - parameters:");
        for (ScriptParameter param : script.getParameters().values()) {
            System.out.println("  + " + param.getName()
                    + ((param.isRequired()) ? " [Required]" : ""));
            System.out.println("    - nice name: " + param.getNicename());
            System.out.println("    - desc: " + param.getDescription());
        }
    }

    private void loadProperties() {
        URL url = coreBundle.getEntry("/src/pipeline.properties");
        Properties props = new Properties(System.getProperties());
        try {
            props.loadFromXML(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.setProperties(props);
    }

    public void populateFakeMessages() {
        MessageManager.getDefault().init();
        for (MessageEvent.Type type : MessageEvent.Type.values()) {
            for (MessageEvent.Cause cause : MessageEvent.Cause.values()) {
                //EventBus.getInstance().publish(
                //        new TaskMessageEvent(
                //                new FakeTrans("fake trans"), "this is a "
                //                        + cause + " " + type + " message",
                //                type, cause, null));
                EventBus.getInstance().publish(
                        new CoreMessageEvent(this, "this is a " + cause + " "
                                + type + " message", type, cause, null));
            }
        }
    }

    public static void populateTestJobs() {
        try {
            DMFCCore core = PipelineGuiPlugin.getDefault().getCore();
            core.newScript(DMFCCore.class.getResource("/../scripts/_dev/dtbook2html.taskScript"));
            
            
            URL url = FileLocator.toFileURL(Platform.getBundle(
                    PipelineGuiPlugin.CORE_ID).getEntry("/scripts/_dev"));
            EFolder devDir = new EFolder(url.toURI());
            Collection devScripts = devDir.getFiles(true, ".+\\.taskScript");
            for (Iterator iter = devScripts.iterator(); iter.hasNext();) {
                File file = (File) iter.next();
                ScriptManager scriptMan = ScriptManager.getDefault();
                Script script = scriptMan.getScript(file.toURI());
                Job job = new Job(script);
                JobManager.getInstance().add(job);
            }

            // URL url = FileLocator
            // .toFileURL(Platform
            // .getBundle(PipelineGuiPlugin.CORE_ID)
            // .getEntry(
            // "/scripts/validation/simple/Daisy202DTBValidator.taskScript"));
            // ScriptManager scriptMan = ScriptManager.getDefault();
            // Script script = scriptMan.getScript(url.getPath());
            // Job job = new Job(script);
            // job.setParameterValue("validatorInputFile",
            // "/Users/Romain/Documents/DAISY/Misc/202dtb/ncc.html");
            // JobManager.getInstance().add(job);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class FakeTransInfo implements TransformerInfo {

        private String name;

        public FakeTransInfo(String name) {
            this.name = name;
        }

        public String getDescription() {
            return "description";
        }

        public Collection getDocumentation() {
            return null;
        }

        public String getName() {
            return name;
        }

        public Collection getParameters() {
            return null;
        }

        public File getTransformerDir() {
            return null;
        }

        public String getNiceName() {
            return name;
        }

        public String getPackageName() {
            return name;
        }

    }

    private class FakeTrans extends Transformer {

        String name;

        public FakeTrans(String name) {
            super(null, false);
            this.name = name;
        }

        @Override
        protected boolean execute(Map parameters)
                throws TransformerRunException {
            return false;
        }

        @Override
        public TransformerInfo getTransformerInfo() {
            return new FakeTransInfo(name);
        }

    }

}
