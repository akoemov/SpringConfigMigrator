package com.akoemov;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander Akoemov on 10/10/2016.
 */
public class ParserXML {

    public static final String BEAN = "bean";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String SCOPE = "scope";
    public static final String LAZY_INIT = "lazy-init";
    public static final String CLASS = "class";
    public static final String REF = "ref";
    public static final String VALUE = "value";
    public static final String PROPERTY = "property";
    public static final String PARENT = "parent";


    public String parsXMLFile(File xmlFile) {

        StringBuffer result = new StringBuffer();

        try {

            String className = "SpringConf";

            createBeginClass(result, className);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName(BEAN);

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    BeanModel model = new BeanModel();

                    if (!eElement.getAttribute(ID).isEmpty()) {
                        model.setId(eElement.getAttribute(ID));
                    }

                    if (!eElement.getAttribute(NAME).isEmpty()) {
                        model.setName(eElement.getAttribute(NAME));
                    }

                    if (!eElement.getAttribute(LAZY_INIT).isEmpty()) {
                        model.setLazyInit(true);
                    }

                    if (!eElement.getAttribute(CLASS).isEmpty()) {
                        model.setClazz(eElement.getAttribute(CLASS));
                    }

                    if (!eElement.getAttribute(PARENT).isEmpty()) {
                        model.setClazz(eElement.getAttribute(CLASS));
                    }

                    NodeList nListProp = eElement.getElementsByTagName(PROPERTY);

                    for (int i = 0; i < nListProp.getLength(); i++) {
                        Node nNodeP = nListProp.item(i);

                        if (nNodeP.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElementP = (Element) nNodeP;
                            Prop prop = new Prop();

                            if (!eElementP.getAttribute(NAME).isEmpty()) {
                                prop.setName(eElementP.getAttribute(NAME));
                            }

                            if (!eElementP.getAttribute(VALUE).isEmpty()) {
                                prop.setValue(eElementP.getAttribute(VALUE));
                            }

                            if (!eElementP.getAttribute(REF).isEmpty()) {
                                prop.setRef(eElementP.getAttribute(REF));
                                prop.setRef(true);
                            }

                            model.getProp().add(prop);
                            model.setSimple(false);
                        }
                    }

                    if (model.isSimple()) {
                        createSimpleBean(result, model);
                    } else {
                        createNotSmpleBean(result, model);
                    }
                }
            }

            createEndClass(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private static void createSimpleBean(StringBuffer result, BeanModel model) {

        result.append("@Bean\n");
        if (model.isLazyInit()) {
            result.append("@Lazy");
        }
        result.append("public " + model.getClazz().getSimpleName() + " " + model.getId() + "() {\n" +
                "return new " + model.getClazz().getSimpleName() + "();\n" +
                "}\n\n");

        result.insert(0, ";\n");
        result.insert(0, model.getClazz().getName());
        result.insert(0, " ");
        result.insert(0, "import");

    }

    private static void createNotSmpleBean(StringBuffer result, BeanModel model) {

        result.append("@Bean\n");
        if (model.isLazyInit()) {
            result.append("@Lazy\n");
        }

        result.append("public " + model.getClazz().getSimpleName() + " " + model.getId() + "() {\n" +
                model.getClazz().getSimpleName() + " " + "bean = new " + model.getClazz().getSimpleName() + "();\n");

        for (Prop prop : model.getProp()) {
            if (prop.isRef()) {
                result.append("bean.set" + prop.getSetterName() + "(" + prop.getRef() + "());\n");
            } else {
                result.append("bean.set" + prop.getSetterName() + "(" + prop.getValue() + ");\n");
            }
        }


        result.append("return bean; \n" +
                "}\n\n");

        result.insert(0, ";\n");
        result.insert(0, model.getClazz().getName());
        result.insert(0, " ");
        result.insert(0, "import");

    }

    private static void createBeginClass(StringBuffer result, String name) {
        result.append("import org.springframework.context.annotation.Bean;\n" +
                "import org.springframework.context.annotation.ComponentScan;\n" +
                "import org.springframework.context.annotation.Configuration;\n");
        result.append("\n");
        result.append("@Configuration\n");
        result.append("public class " + name + "{\n");

    }

    private static void createEndClass(StringBuffer result) {
        result.append("}");
    }
}

class BeanModel {

    private boolean isSimple = true;

    private String id;

    private String name;

    private Class clazz;

    private List<Prop> prop = new ArrayList<Prop>();

    private boolean lazyInit;

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getClazz() {
        return clazz;
    }

    public boolean isSimple() {
        return isSimple;
    }

    public void setSimple(boolean simple) {
        isSimple = simple;
    }

    public void setClazz(String clazz) {
        try {
            this.clazz = Class.forName(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<Prop> getProp() {
        return prop;
    }

    public void setProp(List<Prop> prop) {
        this.prop = prop;
    }
}

class Prop {

    private boolean isRef;

    private String name;

    private String ref;

    private String value;

    public boolean isRef() {
        return isRef;
    }

    public void setRef(boolean ref) {
        isRef = ref;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSetterName() {
        if (name != null) {
            return name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
        } else return name;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}