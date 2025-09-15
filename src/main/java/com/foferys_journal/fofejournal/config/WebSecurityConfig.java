package com.foferys_journal.fofejournal.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {


    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    // UserDetailsService è un'interfaccia che Spring Security usa per recuperare i dettagli dell'utente dal database.
    // Questa dipendenza viene iniettata tramite il costruttore WebSecurityConfig(UserDetailsService userDetailsService)
    // Verrà utilizzata per ottenere i dettagli dell'utente durante il processo di autenticazione.
    private final UserDetailsService userDetailsService;

    //ignezione della dipendenza UserDetailsService con costruttore
    public WebSecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    // Spring Security utilizza filtri per gestire l'autenticazione e l'autorizzazione
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        //usiamo httpsecurity che ha diversi metodi che possiamo usare
        /* se ritornamo solo il build che invia il valore predefinito e se provo ad accedere a un mapping non mi richiede un login 
        perché lo baypassa senza errori perché non lo stiamo gestendo*/

        // facciamo in modo di gestire l'autenticazione, in questo caso qualsiasi richiesta sarà autenticata con http attraverso i dati impostati nel file di confgurazione
        httpSecurity
            //CSRF (Cross-Site Request Forgery) è una protezione che impedisce attacchi in cui un utente autenticato viene forzato a eseguire azioni non volute 
            //attraverso un elemento nascosto - disabilitato con csrf.disable()
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(  //→ Specifica gli URL pubblici accessibili senza autenticazione:
                request -> request
                .requestMatchers("/signupProcess", "/signup", "/login", "/formlogin", "/css/**","/static/**", "/js/**").permitAll() // Pagine pubblicherr
                .anyRequest().authenticated() // Tutte le altre pagine richiedono autenticazione

            ) //Configurazione della pagina di login
            .oauth2Login( oauth2 -> oauth2
                .loginPage("/formlogin")  // Pagina personalizzata per il login
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)) // 👈 Usa il nostro servizio
                .defaultSuccessUrl("/", true)  // 👈 Reindirizza sempre alla home dopo il login
            )
            .formLogin(form -> form
                .loginPage("/formlogin") //→ Specifica la pagina personalizzata per il login.
                .loginProcessingUrl("/login")
                .failureUrl("/login?error=true") // 👈 Passa l'errore se il login fallisce
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout") // Redirect dopo logout
                .permitAll()
            );

        return httpSecurity.build();
    }

    //authenticationProvider gestisce l'autenticazione degli utenti.
    /*Spring Security lo utilizza automaticamente perché è annotato con @Bean. Quando Spring Security deve autenticare un utente, cerca un AuthenticationProvider 
    nel contesto di Spring. Trova il nostro DaoAuthenticationProvider definito nel metodo authenticationProvider() e Usa questo provider per verificare 
    le credenziali e restituire un oggetto Authentication valido. */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); //DaoAuthenticationProvider è il provider predefinito di Spring Security per l'autenticazione basata su database.
        provider.setUserDetailsService(userDetailsService); //→ Indica a Spring Security come ottenere gli utenti dal database
        provider.setPasswordEncoder(passwordEncoder()); // 👈 REGISTRA IL PASSWORD ENCODER e Specifica come le password vengono criptate
        return provider;
    }

    //AuthenticationManager gestisce il processo di autenticazione
    @Bean
    public org.springframework.security.authentication.AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
