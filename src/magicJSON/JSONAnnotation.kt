package magicJSON

/**
 * Annotation used customize an object property name
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class JSONCustomField(val name: String)

/**
 * Annotation used to exclude a given object property from being serialized
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class JSONExcludeItem
