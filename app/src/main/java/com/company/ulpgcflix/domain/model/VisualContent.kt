package com.company.ulpgcflix.domain.model

import com.company.ulpgcflix.domain.model.enums.kindVisualContent
import com.company.ulpgcflix.ui.model.CategoryUi

data class VisualContent(
    private val id: String,
    private val title: String,
    private val overview: String,
    private val image: String,
    private val assessment: Double,
    private val kind: kindVisualContent,
    private val category: List<CategoryUi>,
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


    val getCategory: List<CategoryUi>
        get() = this.category

    val isAdultContent: Boolean
        get() = this.isAdult
}