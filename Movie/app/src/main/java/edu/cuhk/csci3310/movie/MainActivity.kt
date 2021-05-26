package edu.cuhk.csci3310.movie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.io.IOException
import java.net.URL


class MainActivity : AppCompatActivity(),Adapter.ItemClickListener  {

    private var itemList = ArrayList<Item>()
    var mAdapter: Adapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalScope.launch(Dispatchers.IO) {
            parseData()
        }

        mAdapter = Adapter(itemList,this)

        recycler_view.adapter = mAdapter

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)


    }

    private fun parseData() = runBlocking{
        val request = launch {
            try {
                val document = Jsoup.connect("https://wmoov.com/movie/showing").get()
                val element = document.select("div[class=each]:has(div[class=right])")

                element.forEach { data ->
                    val id = data.select("h3>a").attr("href").toString().split("/").toTypedArray()[3]
                    val name = data.select("h3>a").text()
                    val label = data.select("p:has(span)").textNodes()[1].toString()
                    var posterViewSrc = "https:"+data.select("div[class=poster_s fblc]>a>div>img").attr("src").toString().replace(".jpg","_y.jpg")
                    val hot = data.select("p:has(span)").textNodes()
                    val hot1 = hot[hot.lastIndex-1].toString().replace(",","").replace(" ","")



                    launch(Dispatchers.IO) {
                        try {
                            val document = Jsoup.connect("https://wmoov.com/movie/post/$id").get()
                            val posterSrc = document.select("a[title=\"電影海報\"]").attr("href").toString()
                            var detail = document.select("div[class=\"movie_bg pt-3\"]>div>p:not([class])")[1].text()
                            val englishName = document.select("p[class=\"intro text-center\"]").text()
                            val englishName1 = englishName.substring(englishName.indexOf("(") + 1, englishName.indexOf(")"))
                            val youtubeLink = document.select("iframe").attr("src").toString()
                            val imdbLink = document.select("a[href*=imdb]").attr("href").toString()
                            val minite = document.select("div[class=\"container\"]>p:contains(片長)").text()
                            var minite1 : String = " "
                            if (minite.count() > 0){
                                minite1 = minite.substring(minite.indexOf("長") + 1, minite.indexOf("分")) + " min"
                            }

                            if (detail.count() ==0){
                                detail = document.select("div[class=\"movie_bg pt-3\"]>div>p:not([class])")[0].text()
                            }

                            val photo = document.select("figure[class=\"mx-auto text-center\"]>img[alt*=電影劇照]")
                            val photoPoster = document.select("figure[class=\"mx-auto text-center\"]>img[alt*=電影海報]")

                            val photoList = ArrayList<String>()

                            photo.forEach {
                                photoList.add("https://wmoov.com"+it.attr("data-src"))
                            }
                            photoPoster.forEach {
                                photoList.add("https://wmoov.com"+it.attr("data-src"))
                            }


                            if (posterSrc.count() > 0){
                                posterViewSrc = "https://wmoov.com$posterSrc"
                            }


                            launch(Dispatchers.IO){

                                var rate : String = ""

                                if (imdbLink != ""){
                                    try {
                                        val document = Jsoup.connect(imdbLink).get()
                                        rate = document.select("span[itemprop=\"ratingValue\"]").text()

                                        if (minite1 == " ") {
                                            minite1 = document.select("div[class=\"txt-block\"]>time").text()
                                        }
                                    }catch (e:IOException){}
                                }else{
                                    try {
                                        val response = URL("https://www.omdbapi.com/?t=$englishName1&apikey=75486d31").readText()
                                        val imdbData = Gson().fromJson(response, IMDBData::class.java)

                                        rate = imdbData.imdbRating
                                        if (minite1 == " " && imdbData.runtime != null){
                                            minite1 = imdbData.runtime
                                        }
                                    }catch (e:IOException){}
                                }

                                if (rate == "N/A"|| rate == null){rate = "0"}

                                itemList.add(Item(hot1.toInt(),name,posterViewSrc,label,minite1,rate.toFloat(),detail,photoList,youtubeLink))
                            }

                        }catch (e:IOException){}

                    }

                }
            } catch (e: IOException) {}
        }
        request.join()

        itemList.sortByDescending  { it.hot }

        runOnUiThread {
            progress_loader.visibility = View.GONE
            mAdapter?.notifyDataSetChanged()
        }

    }

    override fun onItemClick(position: Int) {
        println(position)
        val intent = Intent(this,MovieDetailActivity::class.java)
        intent.putExtra("title",itemList[position].title)
        intent.putExtra("label",itemList[position].label)
        intent.putExtra("time",itemList[position].minute)
        intent.putExtra("detail",itemList[position].detail)
        intent.putExtra("youTube",itemList[position].youtubeLink)
        intent.putExtra("rate",itemList[position].rate)
        intent.putExtra("posterSrc",itemList[position].postViewSrc)

        intent.putStringArrayListExtra("photoList",
            itemList[position].photoList as java.util.ArrayList<String>?
        )


        startActivity(intent)
    }
}
