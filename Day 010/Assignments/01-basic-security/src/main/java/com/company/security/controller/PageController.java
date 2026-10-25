package com.company.security.controller;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

/**
 * Browser pages for the form-login flow: home, the CUSTOM login page and the
 * admin dashboard. The HTML is written inline to avoid a template engine.
 *
 * The login and logout forms both contain the CSRF token as a hidden field.
 * Spring Security puts the token in a request attribute; Spring MVC hands it to
 * these methods as a CsrfToken parameter.
 */
@Controller
public class PageController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String home() {
        return page("Employee portal", """
                <h1>Employee portal</h1>
                <p>Public status: <a href="/api/public/hello">/api/public/hello</a></p>
                <p>Administrators: <a href="/admin/dashboard">open the dashboard</a> (you will be asked to log in)</p>
                """);
    }

    @GetMapping(value = "/login", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String login(@RequestParam(name = "error", required = false) String error,
                        @RequestParam(name = "logout", required = false) String logout,
                        CsrfToken csrf) {
        String message = "";
        if (error != null) {
            // Custom failure response: one message for "unknown user" and "wrong password"
            message = "<p class=\"msg error\" role=\"alert\">Invalid username or password.</p>";
        } else if (logout != null) {
            message = "<p class=\"msg ok\" role=\"status\">You have been logged out.</p>";
        }
        return page("Sign in", """
                <h1>Sign in</h1>
                %s
                <form method="post" action="/login">
                  <label for="username">Username</label>
                  <input id="username" name="username" autocomplete="username" required autofocus>
                  <label for="password">Password</label>
                  <input id="password" name="password" type="password" autocomplete="current-password" required>
                  <input type="hidden" name="%s" value="%s">
                  <button type="submit">Sign in</button>
                </form>
                """.formatted(message, csrf.getParameterName(), csrf.getToken()));
    }

    @GetMapping(value = "/admin/dashboard", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String dashboard(Authentication authentication, CsrfToken csrf) {
        return page("Admin dashboard", """
                <h1>Admin dashboard</h1>
                <p>Signed in as <strong>%s</strong> (%s)</p>
                <p>This page is only for ROLE_ADMIN. The login is kept in an HTTP session (cookie JSESSIONID).</p>
                <form method="post" action="/logout">
                  <input type="hidden" name="%s" value="%s">
                  <button type="submit">Log out</button>
                </form>
                """.formatted(HtmlUtils.htmlEscape(authentication.getName()),
                HtmlUtils.htmlEscape(authentication.getAuthorities().toString()),
                csrf.getParameterName(), csrf.getToken()));
    }

    private static String page(String title, String body) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>%s</title>
                <style>
                  body { font-family: system-ui, sans-serif; max-width: 26rem; margin: 3rem auto; padding: 0 1rem; color: #1f2937; }
                  label { display: block; margin-top: 1rem; font-weight: 600; }
                  input { width: 100%%; box-sizing: border-box; padding: .6rem; font: inherit; border: 1px solid #6b7280; border-radius: 6px; }
                  button { margin-top: 1.25rem; padding: .6rem 1.2rem; font: inherit; font-weight: 600; border: 0; border-radius: 6px; background: #1d4ed8; color: #fff; cursor: pointer; }
                  .msg { padding: .6rem .8rem; border-radius: 6px; }
                  .error { background: #fee2e2; color: #991b1b; }
                  .ok { background: #dcfce7; color: #166534; }
                </style>
                </head>
                <body>
                %s
                </body>
                </html>
                """.formatted(title, body);
    }
}
