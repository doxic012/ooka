package org.sfisc12s.lzu_web;

import java.io.Serializable;

/**
 * Created by steve on 07.03.16.
 */
public class LZUController implements Serializable {

    public LZUController() {

    }

//    private RuntimeEnvironment re = RuntimeEnvironment.getInstance();

//    public void handleFileUpload(FileUploadEvent event) {
//        FacesMessage message = new FacesMessage("Successful", event.getFile().getFileName() + " is uploaded.");
//        FacesContext.getCurrentInstance().addMessage(null, message);
//    }

    public void test() {
        System.out.println("Test");
    }

//    @EJB
//    private ComponentFacade componentFacade;

//    @EJB
//    private FileUploader uploader;
//
//    public FileUploader getUploader() {
//        return uploader;
//    }

//    public ComponentFacade getComponentFacade() {
//        return componentFacade;
//    }

    //    public boolean isFileValid() {
//        if (componentFile == null)
//            return false;
//
//        String fileName = componentFile.getFileName();
//        return fileName.endsWith(".jar") || fileName.endsWith(".class");
//    }
//
//    public void addComponent() {
//
//    }
//
//    public void loadLibrary() {
//
//    }

}
