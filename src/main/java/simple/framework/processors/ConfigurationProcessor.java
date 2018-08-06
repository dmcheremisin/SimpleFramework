package simple.framework.processors;

import simple.framework.annotations.Service;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationProcessor {
    private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();
    public static final String BEAN = "bean";
    public static final String NAME = "name";
    public static final String CLASS = "class";
    private static Map<String, String> configuration = new HashMap<>();
    private static Map<String, Object> classes = new HashMap<>();

    private XMLStreamReader reader;

    public ConfigurationProcessor() {
        try {
            String file = this.getClass().getClassLoader().getResource("context.xml").getFile();
            reader = FACTORY.createXMLStreamReader(new FileInputStream(file));
            this.processConfiguration();
            this.loadConfigurationContext();
        } catch (Exception e) {
            System.out.println("Something went wrong with ConfigurationProcessor creation");
            throw new RuntimeException(e.getMessage());
        }
    }

    private void processConfiguration() {
        try {
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT && BEAN.equals(reader.getLocalName())) {
                    String name = reader.getAttributeValue(null, NAME);
                    String clazz = reader.getAttributeValue(null, CLASS);
                    configuration.put(name, clazz);
                }
            }
        } catch (XMLStreamException e) {
            System.out.println("Something went wrong with xml processing");
            throw new RuntimeException(e.getMessage());
        }
    }

    private void loadConfigurationContext() {
        configuration.values().forEach(name -> {
            try {
                Class<?> aClass = Class.forName(name);
                if(aClass.isAnnotationPresent(Service.class)){
                    Object object = aClass.newInstance();
                    classes.put(name, object);
                }
            } catch (ClassNotFoundException e) {
                System.err.println("Class not found: " + name);
            } catch (IllegalAccessException | InstantiationException e) {
                System.err.println("Class can't be created: " + name);
            }
        } );
    }

    Map<String, Object> getContext() {
        return classes;
    }
}
