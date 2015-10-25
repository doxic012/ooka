package org.bonn.ooka.runtime.util.entity;

import org.bonn.ooka.runtime.util.Command;

public class CommandLoadJar implements Command<String> {
    @Override
    public void execute(String classPath) {

        System.out.println("load class with arguments: '" + classPath + "'");
        String[] args = classPath.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        System.out.println(args.length);
        for(String s : args)
            System.out.println(s);
//        if (classPath == null || classPath.length() == 0) {
//            Scanner scan = new Scanner(System.in);
//            System.out.println("Path to class:");
//
//            classPath = scan.nextLine();
//            System.out.println("param input: " + classPath);
//        }
//
//        List<URL> urls = new ArrayList<>();
//        try {
//            System.out.println(classPath.split(",").length);
//            for (String path : classPath.split(",")) {
//                urls.add(new URL("file://" + path));
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        URLClassLoader loader = new URLClassLoader((URL[]) urls.toArray());
    }
}
