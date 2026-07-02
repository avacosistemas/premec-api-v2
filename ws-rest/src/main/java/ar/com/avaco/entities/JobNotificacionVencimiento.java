package ar.com.avaco.entities;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Service;

import ar.com.avaco.service.NotificacionVencimientoService;

@Service
public class JobNotificacionVencimiento implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		NotificacionVencimientoService notificacionVencimientoService = (NotificacionVencimientoService) context.getJobDetail().getJobDataMap().get("NotificacionVencimientoService");
		notificacionVencimientoService.enviarNotificaciones();
		
	}


}
