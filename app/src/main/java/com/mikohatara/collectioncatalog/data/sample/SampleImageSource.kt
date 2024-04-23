package com.mikohatara.collectioncatalog.data.sample

import com.mikohatara.collectioncatalog.R

class SampleImageSource() {
    fun loadSampleImages(): List<SampleImage> {
        return listOf<SampleImage>(
            SampleImage(R.drawable.fin_abc012_a),
            SampleImage(R.drawable.fin_abc012_b),
            SampleImage(R.drawable.j_a1234_a),
            SampleImage(R.drawable.j_sa0123_a),
            SampleImage(R.drawable.su_1234abv_a),
            SampleImage(R.drawable.su_a1234bv_a)
        )
    }
}