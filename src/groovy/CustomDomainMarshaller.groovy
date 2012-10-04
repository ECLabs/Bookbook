// CustomDomainMarshaller.groovy in src/groovy:
import bookbook.domain.Book
import grails.converters.JSON;
import org.codehaus.groovy.grails.web.converters.ConverterUtil;
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException;
import org.codehaus.groovy.grails.web.converters.marshaller.ObjectMarshaller;
import org.codehaus.groovy.grails.web.json.JSONWriter;
import org.springframework.beans.BeanUtils;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class CustomDomainMarshaller implements ObjectMarshaller<JSON> {

    static EXCLUDED = ['metaClass',
		'class',
		'id',
		'version',
		'underlyingNode',
		'underlyingRel',
		'attached',
		'constraints',
		'errors',
		'errorCount',
		'fieldError',
		'fieldErrors',
		'fieldErrorCount',
		'globalError',
		'globalErrorCount',
		'messageCodesResolver',
		'globalErrors',
		'properties',
		'transients',
		'allErrors',
		'embedded',
		'nestedPath',
		'model',
		'transient',
		'propertyAccessor',
		'propertyEditorRegistry'];

    public boolean supports(Object object) {
		def grailsApplication = new Book().domainClass.grailsApplication
		
		// TODO: limit only to Domain classes

        return object instanceof GroovyObject;
    }

    public void marshalObject(Object o, JSON json) throws ConverterException {
        JSONWriter writer = json.getWriter();
        try {
            writer.object();
			
            def properties = BeanUtils.getPropertyDescriptors(o.getClass());
            for (property in properties) {
                String name = property.getName();
                if(!EXCLUDED.contains(name)) {
					println "name --> ${name}"
                    def readMethod = property.getReadMethod();
                    if (readMethod != null) {
                        def value = readMethod.invoke(o, (Object[]) null);
                        writer.key(name);
                        json.convertAnother(value);
                    }
				}
            }
            
			Field[] fields = o.getClass().getDeclaredFields();
			for (Field field : fields) {
 				int modifiers = field.getModifiers();
				if (Modifier.isPublic(modifiers)
					&& !(Modifier.isStatic(modifiers)
						|| Modifier.isTransient(modifiers))) {
					writer.key(field.getName());
					json.convertAnother(field.get(o));
				}
			}
            writer.endObject();
        } catch (ConverterException ce) {
            throw ce;
        } catch (Exception e) {
            throw new ConverterException("Exception in CustomDomainMarshaller", e);
        }
    }
}