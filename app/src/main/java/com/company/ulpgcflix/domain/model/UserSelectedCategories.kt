package com.company.ulpgcflix.domain.model

import com.company.ulpgcflix.ui.model.CategoryUi

data class UserSelectedCategories(
    val selectedMovieCategories: Set<CategoryUi>,
)