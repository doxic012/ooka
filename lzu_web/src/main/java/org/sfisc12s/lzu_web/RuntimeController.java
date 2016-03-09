package org.sfisc12s.lzu_web;

import org.primefaces.event.FileUploadEvent;
import org.sfisc12s.lzu_web.util.JsfUtil;
import org.sfisc12s.lzu_web.util.PaginationHelper;
import org.sfisc12s.test.NewEntity;
import org.sfisc12s.test.NewEntityFacade;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ResourceBundle;

@Named("runtime")
@SessionScoped
public class RuntimeController implements Serializable {

    public RuntimeController() {
    }

    public void loadLibrary(FileUploadEvent event) {
        test = "load lib: "+event.getFile().getFileName();
    }
    public void addComponent(FileUploadEvent event) {
        test = "add cmp: "+event.getFile().getFileName();
    }

    private String test = "test";

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }
}
