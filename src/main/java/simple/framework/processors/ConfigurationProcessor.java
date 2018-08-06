package simple.framework.processors;

import simple.framework.annotations.Service;
import simple.framework.util.Tupple;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ConfigurationProcessor {
    private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();
    public static final String BEAN = "bean";
    public static final String NAME = "name";
    public static final String CLASS = "class";
    public static final String ARG = "arg";
    public static final String REF = "ref";
    private static Map<Tupple<String,String>, List<String>> configuration = new HashMap<>();
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
                if (event == XMLEvent.START_ELEMENT && BEAN.equalsIgnoreCase(reader.getLocalName())) {
                    String name = reader.getAttributeValue(null, NAME);
                    String clazz = reader.getAttributeValue(null, CLASS);
                    Tupple<String, String> tupple = new Tupple<>(name, clazz);
                    System.out.println("tupple: " + tupple);
                    List<String> args = new ArrayList<>();
                    while (reader.hasNext()) {
                        int internalEvent = reader.next();
                        if (internalEvent == XMLEvent.START_ELEMENT && ARG.equalsIgnoreCase(reader.getLocalName())) {
                            args.add(reader.getAttributeValue(null, REF));
                        }
                        if(internalEvent == XMLEvent.END_ELEMENT){
                            break;
                        }
                    }
                    System.out.println("args " + args);
                    configuration.put(tupple, args);
                }
            }
        } catch (XMLStreamException e) {
            System.out.println("Something went wrong with xml processing");
            throw new RuntimeException(e.getMessage());
        }
    }


    private void loadConfigurationContext() {
        configuration.entrySet().forEach(entry -> {
            try {
                Tupple<String, String> tupple = entry.getKey();
                Class<?> aClass = Class.forName(tupple.getSecond());
                if(aClass.isAnnotationPresent(Service.class)){
                    Object object = null;
                    List<String> args = entry.getValue();
                    if(!args.isEmpty()){
                        int size = args.size();
                        Class<?>[] constructorParams = new Class<?>[size];
                        Object[] constructorValues = new Object[size];
                        for(int i = 0; i < size; i++){
                            String className = args.get(i);
                            Tupple<String, String> classTupple = new Tupple<>(className, null);
                            Tupple<String, String> foundTupple = configuration.keySet().stream().filter(key -> key.equals(classTupple)).findFirst().get();
                            constructorParams[i] = Class.forName(foundTupple.getSecond());
                            constructorValues[i] = classes.get(className);
                        }
                        Constructor<?> constructor = aClass.getConstructor(constructorParams);
                        if(constructor != null){
                            object = constructor.newInstance(constructorValues);
                        } else {
                            throw new NoSuchMethodException("Constructor for: " + tupple.getFirst() + " not found with args: " + Arrays.asList(constructorParams));
                        }
                    } else {
                        object = aClass.newInstance();
                    }
                    classes.put(tupple.getFirst(), object);
                }
            } catch (ClassNotFoundException e) {
                System.err.println("Class not found: " + entry.getKey().getFirst());
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                System.err.println("Class can't be created: " + entry.getKey().getFirst());
            }
        } );
    }

    Map<String, Object> getContext() {
        return classes;
    }
}
