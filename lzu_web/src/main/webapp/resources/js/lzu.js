var component_global_active = "col-md-6";
var component_global_inactive = "col-md-3";

var toggleComponent = function (o) {
    console.log(o);
    var panel = $(o);
    var allPanels = $(".lzu-component-panel");

    if (!panel.hasClass("active")) {
        allPanels.removeClass("active");
        allPanels.removeClass(component_global_active);
        allPanels.addClass(component_global_inactive);

        panel.addClass("active");
        panel.addClass(component_global_active);
        panel.removeClass(component_global_inactive);
    }
};
