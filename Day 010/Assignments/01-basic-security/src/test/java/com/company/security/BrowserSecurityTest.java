package com.company.security;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** The browser chain: custom login page, form login, session, CSRF, ADMIN-only dashboard, logout. */
@SpringBootTest
@AutoConfigureMockMvc
class BrowserSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void dashboard_withoutLogin_redirectsABrowserToTheCustomLoginPage() throws Exception {
        mockMvc.perform(get("/admin/dashboard").accept(MediaType.TEXT_HTML))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void loginPage_isPublic_andContainsTheCsrfToken() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("name=\"_csrf\"")));
    }

    @Test
    void formLogin_asAdmin_succeeds_andGoesToTheDashboard() throws Exception {
        mockMvc.perform(formLogin("/login").user("admin").password("Admin@123"))   // formLogin() adds the CSRF token
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(authenticated().withUsername("admin").withRoles("ADMIN", "USER"));
    }

    @Test
    void formLogin_withWrongPassword_returnsToTheLoginPageWithAnError() throws Exception {
        mockMvc.perform(formLogin("/login").user("admin").password("nope"))
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    void loginPage_showsTheFailureMessage() throws Exception {
        mockMvc.perform(get("/login").param("error", ""))
                .andExpect(content().string(containsString("Invalid username or password.")));
    }

    /** CSRF is ON for the browser chain: a login POST without the token is refused. */
    @Test
    void formLogin_withoutCsrfToken_isForbidden() throws Exception {
        mockMvc.perform(post("/login").param("username", "admin").param("password", "Admin@123"))
                .andExpect(status().isForbidden());
    }

    @Test
    void dashboard_asAdmin_isShown() throws Exception {
        mockMvc.perform(get("/admin/dashboard").with(user("admin").roles("ADMIN", "USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Signed in as <strong>admin</strong>")));
    }

    /** Authenticated but not allowed: 403, not a login redirect. */
    @Test
    void dashboard_asAUserWithoutAdminRole_isForbidden() throws Exception {
        mockMvc.perform(get("/admin/dashboard").with(user("alice").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void logout_endsTheSession_andShowsTheLoginPage() throws Exception {
        mockMvc.perform(logout("/logout"))   // POST with CSRF token
                .andExpect(redirectedUrl("/login?logout"))
                .andExpect(unauthenticated());
    }

    @Test
    void logout_withGet_isNotAllowed() throws Exception {
        // Logout must be a POST with the CSRF token, so a link on another site cannot log users out
        mockMvc.perform(get("/logout").with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(status().isNotFound());
    }
}
