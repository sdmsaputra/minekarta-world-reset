package com.minekarta.worldreset.scheduler;

import com.minekarta.worldreset.MinekartaWorldReset;
import com.minekarta.worldreset.task.WorldResetTask;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SchedulerService {

    private static final Logger logger = Logger.getLogger("MinekartaWorldReset");
    private final MinekartaWorldReset plugin;
    private Scheduler scheduler;
    private JobKey jobKey;

    public SchedulerService(MinekartaWorldReset plugin) {
        this.plugin = plugin;
    }

    public void start() {
        try {
            scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduleWorldReset();
        } catch (SchedulerException e) {
            logger.log(Level.SEVERE, "Failed to start scheduler", e);
        }
    }

    public void stop() {
        try {
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (SchedulerException e) {
            logger.log(Level.SEVERE, "Failed to stop scheduler", e);
        }
    }

    public void reload() {
        try {
            if (scheduler != null && scheduler.isStarted()) {
                scheduler.deleteJob(jobKey);
                scheduleWorldReset();
            }
        } catch (SchedulerException e) {
            logger.log(Level.SEVERE, "Failed to reload scheduler", e);
        }
    }

    private void scheduleWorldReset() {
        String schedule = plugin.getSchedule();
        jobKey = new JobKey("worldResetJob", "minekarta");
        JobDetail job = JobBuilder.newJob(WorldResetJob.class)
                .withIdentity(jobKey)
                .usingJobData("plugin", plugin.getName())
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("worldResetTrigger", "minekarta")
                .withSchedule(CronScheduleBuilder.cronSchedule(schedule)
                        .inTimeZone(TimeZone.getDefault()))
                .build();

        try {
            scheduler.scheduleJob(job, trigger);
            logger.info("World reset scheduled with cron expression: " + schedule);
        } catch (SchedulerException e) {
            logger.log(Level.SEVERE, "Failed to schedule world reset", e);
        }
    }

    public Date getNextResetTime() {
        try {
            if (scheduler != null && scheduler.isStarted()) {
                return scheduler.getTrigger(new TriggerKey("worldResetTrigger", "minekarta")).getNextFireTime();
            }
        } catch (SchedulerException e) {
            logger.log(Level.SEVERE, "Failed to get next reset time", e);
        }
        return null;
    }

    public static class WorldResetJob implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            MinekartaWorldReset plugin = (MinekartaWorldReset) Bukkit.getPluginManager().getPlugin(context.getJobDetail().getJobDataMap().getString("plugin"));
            if (plugin != null) {
                Bukkit.getScheduler().runTask(plugin, plugin::triggerReset);
            }
        }
    }
}
