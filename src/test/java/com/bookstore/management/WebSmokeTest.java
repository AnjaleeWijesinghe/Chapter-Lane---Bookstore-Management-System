package com.bookstore.management;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class WebSmokeTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void publicPagesRender() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/catalog"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    @Test
    void protectedPagesRedirectWhenAnonymous() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?required=customer"));

        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?required=admin"));
    }

    @Test
    void customerPagesRenderWithCustomerSession() throws Exception {
        mockMvc.perform(get("/profile").sessionAttr("customerId", "CUS-A1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/cart").sessionAttr("customerId", "CUS-A1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/orders").sessionAttr("customerId", "CUS-A1"))
                .andExpect(status().isOk());
    }

    @Test
    void adminPagesRenderWithAdminSession() throws Exception {
        mockMvc.perform(get("/admin").sessionAttr("adminId", "ADM-A1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/admin/users").sessionAttr("adminId", "ADM-A1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/admin/books").sessionAttr("adminId", "ADM-A1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/admin/orders").sessionAttr("adminId", "ADM-A1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/admin/admins").sessionAttr("adminId", "ADM-A1"))
                .andExpect(status().isOk());
    }

    @Test
    void loginFlowsRedirectToExpectedDashboards() throws Exception {
        mockMvc.perform(post("/login")
                        .param("role", "customer")
                        .param("email", "maya@chapterlane.com")
                        .param("password", "maya123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/catalog"));

        mockMvc.perform(post("/login")
                        .param("role", "admin")
                        .param("email", "admin@chapterlane.com")
                        .param("password", "admin123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }
}
