package com.tlcsdm.rcppt.agent;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.log.StaticLog;
import javassist.*;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: unknowIfGuestInDream
 * @date: 2023/4/2 21:15
 */
public class PreMainGcAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        long timeTnterval = 120;
        long initialDelay = 300;
        boolean hasGcLog = true;
        if (agentArgs != null) {
            String[] strings = agentArgs.split(",");
            for (String str : strings) {
                if (str.contains("intervalTime=")) {
                    timeTnterval = Long.parseLong(str.substring(13));
                    continue;
                }
                if (str.contains("initialDelay=")) {
                    initialDelay = Long.parseLong(str.substring(13));
                    continue;
                }
                if (str.contains("hasGcLog=")) {
                    hasGcLog = Boolean.parseBoolean(str.substring(9));
                }
            }
        }
        boolean finalHasGcLog = hasGcLog;
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = ThreadUtil.createScheduledExecutor(1);
        scheduledThreadPoolExecutor.setThreadFactory(new BasicThreadFactory.Builder().namingPattern("rcpttAgent").daemon(true).build());
        ThreadUtil.schedule(scheduledThreadPoolExecutor, () -> {
            if (finalHasGcLog) {
                StaticLog.info("RcpttAgent execute gc");
            }
            Runtime.getRuntime().gc();
        }, initialDelay, timeTnterval, TimeUnit.SECONDS, false);
        //inst.addTransformer(new DefineTransformer(), true);
    }

    static class DefineTransformer implements ClassFileTransformer {

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            //System.out.println("premain load Class:" + className);
            if ("com/tlcsdm/smc/codeDev/DmaTriggerSourceCode".equals(className)) {
                try {
                    ClassPool pool = ClassPool.getDefault();
                    CtClass ctClass = pool.get("com.tlcsdm.smc.codeDev.DmaTriggerSourceCode");
                    CtField ctField = ctClass.getDeclaredField("test");
                    ctClass.removeField(ctField);
                    CtField f = CtField.make("private final String test = \"world\";", ctClass);
                    ctClass.addField(f);
                    ctClass.toClass();
                } catch (NotFoundException | CannotCompileException e) {
                    e.printStackTrace();
                }
            }
            return classfileBuffer;
        }
    }
}
