package top.cmoon.tools.anno.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("top.cmoon.tools.anno.processor.Conflict")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ConflictAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        annotations.forEach(conflictAnnotation -> {

            // Annotations With Conflict annotation
            Set<? extends Element> annotationAnnotatedWithConflict = roundEnv.getElementsAnnotatedWith(conflictAnnotation);
            annotationAnnotatedWithConflict.forEach(annotation -> {

                // conflict names
                Set<String> opponentAnnotationNames = getOpponentAnnotationNames(annotation);

                // conflict check
                conflictCheck(roundEnv, (TypeElement) annotation, opponentAnnotationNames);
            });
        });

        return true;
    }

    /**
     * example:
     * <pre>
     *
     * {@literal @}Conflict({Update.class, Delete.class})
     *  public @interface Query{
     *
     *  }
     * </pre>
     * <p>
     * this method will return a Set of String {Update, Delete}
     *
     * @param annotation
     * @return
     */
    private Set<String> getOpponentAnnotationNames(Element annotation) {
        Set<String> opponentAnnotationNames = new HashSet<>(8);

        AnnotationMirror conflictAnnotationStatement = annotation.getAnnotationMirrors()
                .stream()
                .filter(e1 -> e1.getAnnotationType().asElement().toString().equals(Conflict.class.getTypeName()))
                .findFirst()
                .orElse(null); // never null

        // value() of Conflict Statement
        conflictAnnotationStatement
                .getElementValues()
                .forEach((key, val) -> {
                    List<? extends AnnotationValue> list = (List<? extends AnnotationValue>) val.getValue();
                    list.forEach((exclusiveAnnotation) ->
                            opponentAnnotationNames.add(exclusiveAnnotation.getValue().toString()));
                });
        return opponentAnnotationNames;
    }


    /**
     * check the annotation whether conflict
     * <p>
     * example, we just assume all the class in the same package, e.g:
     * <p>
     * annotations:
     * <pre>
     *
     * {@literal @}Conflict(Delete.class)
     *  public @interface Query{}
     *
     *  public @interface Delete{}
     *
     * </pre>
     * use annotations:
     * <pre>
     *
     *
     * public class UserDao{
     *
     *     {@literal @}Query
     *     {@literal @}Delete
     *      public void delete(){}
     *
     * }
     * </pre>
     * <p>
     * this will compile fail, a compile fail error will raise,
     * because @Query is annotated by @Conflict with value  Delete.class,
     * so the @Query and @Delete annotation can't be present in the same element
     *
     * @param roundEnv
     * @param annotation
     * @param opponentAnnotationNames
     */
    private void conflictCheck(RoundEnvironment roundEnv, TypeElement annotation, Set<String> opponentAnnotationNames) {
        Set<? extends Element> needCheckElements = roundEnv.getElementsAnnotatedWith(annotation);
        needCheckElements.forEach(element -> {

            List<? extends AnnotationMirror> existConflict = element
                    .getAnnotationMirrors()
                    .stream()
                    .filter(a -> opponentAnnotationNames.contains(a.getAnnotationType().asElement().toString()))
                    .collect(Collectors.toList());

            if (existConflict.size() > 0) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "********************  compile error  ***************************");
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, annotation.getQualifiedName() + " is conflict with " + existConflict.toString(), element);
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "********************  compile error  ***************************");
            }

        });
    }

}
