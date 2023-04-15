package cds.fileuploadproject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class MyHttpSessionListener implements HttpSessionListener {

    private static final Logger logger = LoggerFactory.getLogger(MyHttpSessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        logger.debug("Session Created with id: {}", event.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        logger.debug("Session Destroyed with id: {}", event.getSession().getId());
    }
}
