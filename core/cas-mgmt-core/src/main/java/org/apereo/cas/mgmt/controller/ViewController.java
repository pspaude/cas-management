package org.apereo.cas.mgmt.controller;

import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.mgmt.authentication.CasUserProfileFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import org.pac4j.core.context.J2EContext;
import org.pac4j.core.profile.ProfileManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * Contoller for rendered views.
 *
 * @author Travis Schmidt
 * @since 6.0
 */
@Controller("viewController")
@RequiredArgsConstructor
@Slf4j
public class ViewController {

    private static final String STATUS = "status";

    private final Service defaultService;
    private final CasUserProfileFactory casUserProfileFactory;


    /**
     * Mapped method to return the manage.html.
     *
     * @return - ModelAndView
     */
    @GetMapping({"management/index.html", "management/", "management"})
    public ModelAndView manage() {
        //ensureDefaultServiceExists();
        val model = new HashMap<String, Object>();
        model.put(STATUS, HttpServletResponse.SC_OK);
        model.put("defaultServiceUrl", this.defaultService.getId());
        return new ModelAndView("management/index", model);
    }

    /**
     * Mapped method to return the register.html.
     *
     * @return - ModelAndView
     */
    @GetMapping({"register/index.html", "register/", "register"})
    public ModelAndView register() {
        val model = new HashMap<String, Object>();
        model.put(STATUS, HttpServletResponse.SC_OK);
        return new ModelAndView("register/index", model);
    }

    /**
     * Mapped method to return the register.html.
     *
     * @return - ModelAndView
     */
    @GetMapping({"oauth/index.html", "oauth/", "oauth"})
    public ModelAndView oauth() {
        val model = new HashMap<String, Object>();
        model.put(STATUS, HttpServletResponse.SC_OK);
        return new ModelAndView("oauth/index", model);
    }

    /**
     * Mapped method to return the register.html.
     *
     * @return - ModelAndView
     */
    @GetMapping({"saml/index.html", "saml/", "saml"})
    public ModelAndView saml() {
        val model = new HashMap<String, Object>();
        model.put(STATUS, HttpServletResponse.SC_OK);
        return new ModelAndView("saml/index", model);
    }

    /**
     * Mapped method to return the register.html.
     *
     * @return - ModelAndView
     */
    @GetMapping({"dashboard/index.html", "dashboard/", "dashboard"})
    public ModelAndView dashboard() {
        val model = new HashMap<String, Object>();
        model.put(STATUS, HttpServletResponse.SC_OK);
        return new ModelAndView("dashboard/index", model);
    }

    /**
     * Root mapping that navigates to managment or register depending on user role.
     *
     * @param request - the request
     * @param response - the response
     * @return - ModelAndView
     */
    @GetMapping({"cas-management", "/", "index.html", "register.html", "manage.html"})
    public ModelAndView root(final HttpServletRequest request, final HttpServletResponse response) {
        val casUserProfile = casUserProfileFactory.from(request, response);
        val reqUrl = request.getRequestURL().toString();
        var url = request.getContextPath();
        if (reqUrl.contains("register.html")) {
            url += "/register";
        } else if (reqUrl.contains("manage.html") && casUserProfile.isAdministrator()) {
            url += "/management";
        } else {
            url += casUserProfile.isAdministrator() ? "/management" : "/register";
        }
        return new ModelAndView(new RedirectView(url));
    }


    /**
     * Authorization failure handling. Simply returns the view name.
     *
     * @return the view name.
     */
    @GetMapping(value = "/authorizationFailure")
    public String authorizationFailureView() {
        return "authorizationFailure";
    }

    /**
     * Logout handling. Simply returns the view name.
     *
     * @param request  the request
     * @param response the response
     * @return the view name.
     */
    @GetMapping(value = "/logout.html")
    public String logoutView(final HttpServletRequest request, final HttpServletResponse response) {
        LOGGER.debug("Invalidating application session...");
        new ProfileManager(new J2EContext(request, response)).logout();
        request.getSession(false).invalidate();
        return "logout";
    }
}
