package edu.cuhk.csci3310.movie

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.lzy.ninegrid.ImageInfo
import com.lzy.ninegrid.NineGridView
import com.lzy.ninegrid.NineGridView.ImageLoader
import com.lzy.ninegrid.preview.NineGridViewClickAdapter
import io.github.armcha.coloredshadow.ShadowImageView
import kotlinx.android.synthetic.main.activity_movie_detail.*


class MovieDetailActivity : AppCompatActivity() {
    private val gridView: NineGridView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)
        setSupportActionBar(myToolbar)
        if (supportActionBar != null){
            Log.d("debug","not null")
            supportActionBar!!.setDisplayHomeAsUpEnabled(true);
            supportActionBar!!.setDisplayShowHomeEnabled(true);
            supportActionBar!!.setDisplayShowTitleEnabled(false);
            myToolbar.setNavigationOnClickListener{onBackPressed()}
        }

        val title:String = intent.getStringExtra("title")
        val label:String = intent.getStringExtra("label")
        val time:String = intent.getStringExtra("time")
        val detail:String = intent.getStringExtra("detail")
        val youTube:String = intent.getStringExtra("youTube")
        val posterSrc:String = intent.getStringExtra("posterSrc")
        val rate:Float = intent.getFloatExtra("rate",0.0F)
        val photoList = intent.getStringArrayListExtra("photoList")

        detailTitle.text = title
        detailTime.text = time
        detailLabel.text = label
        detailRate.max = 10
        detailRate.rating = rate/2
        detailRate.stepSize=0.2F
        if (rate ==0.0F){
            detailRateText.text = "N/A"
        }else{
            detailRateText.text = rate.toString()
        }
//        detailDetail.text = detail
        detailDetail.setTextMaxLength(100)
        detailDetail.setSeeMoreText("閱讀更多","收回")
        detailDetail.setContent(detail)

        fab.setOnClickListener {
            println("onclick")
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(youTube)
                )
            )

        }


        Glide.with(this)
            .load(photoList[0])
            .placeholder(R.drawable.loading)
            .into(backGroundImage)

        Glide.with(this)
            .load(posterSrc)
            .placeholder(R.drawable.loading)
            .into(object : CustomViewTarget<ShadowImageView, Drawable>(detailPoster) {

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    detailPoster.radiusOffset = 0.5f
                    detailPoster.setImageDrawable(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    Log.d("list", "load fail  $detailPoster")
                }

                override fun onResourceCleared(placeholder: Drawable?) {
                    Log.d("list", "onResourceCleared  $detailPoster")
                    detailPoster.setImageDrawable(placeholder)

                }
            })




        NineGridView.setImageLoader(GlideImageLoader())
        val imageInfo: ArrayList<ImageInfo> = ArrayList()
        photoList.forEach{
            val info = ImageInfo()
            info.setThumbnailUrl(it)
            info.setBigImageUrl(it)
            imageInfo.add(info)
        }

        nineGrid.setAdapter(NineGridViewClickAdapter(this,imageInfo))
    }
}

class GlideImageLoader : ImageLoader {
    override fun onDisplayImage(
        context: Context,
        imageView: ImageView,
        url: String
    ) {
        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.loading)
            .into(imageView)
    }

    override fun getCacheImage(url: String): Bitmap? {
        return null
    }
}
