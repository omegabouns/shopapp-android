package com.client.shop.ui.blog

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebSettings
import com.client.shop.R
import com.client.shop.ShopApplication
import com.client.shop.ext.fitHtmlFrames
import com.client.shop.ext.fitHtmlImages
import com.client.shop.ui.blog.contract.ArticlePresenter
import com.client.shop.ui.blog.contract.ArticleView
import com.client.shop.ui.blog.di.BlogModule
import com.domain.entity.Article
import com.shopify.ShopifyWrapper
import com.ui.base.lce.BaseActivity
import com.ui.ext.shareText
import kotlinx.android.synthetic.main.activity_article.*
import javax.inject.Inject


class ArticleActivity :
    BaseActivity<Article, ArticleView, ArticlePresenter>(),
    ArticleView {

    @Inject
    lateinit var articlePresenter: ArticlePresenter

    private var shareUrl: String? = null
    private var shareMenuItem: MenuItem? = null

    companion object {
        private const val EXTRA_ARTICLE_ID = "extra_article_id"

        fun getStartIntent(context: Context, articleId: String): Intent {
            val intent = Intent(context, ArticleActivity::class.java)
            intent.putExtra(EXTRA_ARTICLE_ID, articleId)
            return intent
        }
    }

    //ANDROID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        loadData(false)
        setupWebView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_article, menu)
        shareMenuItem = menu.findItem(R.id.share)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            return when (item.itemId) {
                R.id.share -> {
                    shareUrl?.let { shareText(it, "Share") }
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //SETUP

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        with(content.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        }
    }

    //INIT

    override fun inject() {
        ShopApplication.appComponent.attachBlogComponent(BlogModule()).inject(this)
    }

    override fun getContentView(): Int = R.layout.activity_article

    override fun createPresenter(): ArticlePresenter = articlePresenter

    //LCE

    override fun showContent(data: Article) {
        super.showContent(data)
        shareMenuItem?.isVisible = true
        shareUrl = data.url
        articleTitle.text = data.title

        content.post {
            val width = (content.width / Resources.getSystem().displayMetrics.density).toInt()
            var html = content.fitHtmlImages(data.contentHTML)
            html = content.fitHtmlFrames(html, (width / 1.5).toInt())
            content.loadDataWithBaseURL(ShopifyWrapper.BASE_URL, html, "text/html", "UTF-8", null)
        }

        author.text = data.author.fullName
        val src = data.image?.src
        image.setImageURI(src)
        image.visibility = if (src != null) View.VISIBLE else View.GONE
    }

    override fun loadData(pullToRefresh: Boolean) {
        super.loadData(pullToRefresh)
        presenter.loadArticles(intent.getStringExtra(EXTRA_ARTICLE_ID))
    }

}
