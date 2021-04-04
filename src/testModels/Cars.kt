package testModels

import JSONAnnotation

@JSONAnnotation
class Cars(
        val brand: String,
        val model: String,
        val year: Int
)