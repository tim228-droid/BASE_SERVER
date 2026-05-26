package kamaz.project.sandbox.config;

import java.util.logging.Logger;

import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class LoginLogger implements ApplicationListener<AuthenticationSuccessEvent>{
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_RESET = "\u001B[0m";
    @Override
    public void onApplicationEvent(@NonNull AuthenticationSuccessEvent event){
        String userName = event.getAuthentication().getName();
        Logger.getLogger("AuthenticationSuccessEvent").info(ANSI_PURPLE + "User ["+userName+"] logged successfully" + ANSI_RESET);
    }

}
