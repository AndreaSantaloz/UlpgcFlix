package com.company.ulpgcflix.model

data class VisualContent(
    private val id: String,
    private val title: String,
    private val overview: String,
    private val image: String,
    private val assessment: Double,
    private val kind: kindVisualContent,
    private val category: List<Category>,
    private val isAdult: Boolean
) {

    val getId: String
        get() = this.id

    val getTitle: String
        get() = this.title

    val getOverview: String
        get() = this.overview

    val getImage: String
        get() = this.image

    val getAssessment: Double
        get() = this.assessment


    val getKind: kindVisualContent
        get() = this.kind


    val getCategory: List<Category>
        get() = this.category

    val isAdultContent: Boolean
        get() = this.isAdult
}