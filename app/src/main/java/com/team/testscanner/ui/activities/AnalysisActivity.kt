package com.team.testscanner.ui.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.team.testscanner.R
import com.team.testscanner.adapters.OptionAdapter
import com.team.testscanner.models.OptionSelector
import com.team.testscanner.models.Question
import com.team.testscanner.models.Quiz

class AnalysisActivity : AppCompatActivity() {
    lateinit var firestore: FirebaseFirestore
    var quizzes : MutableList<Quiz>? = null
    var questions: MutableMap<String, Question>? = null
    var index = 1
    lateinit var btnNext : Button
    lateinit var btnPrevious : Button
    lateinit var btnSubmit : Button
    lateinit var questionImageView : ImageView
    var mode : Int = 0
//    val optionSelector = OptionSelector()
    lateinit var optionSelectorList : MutableList<OptionSelector>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis)
        btnPrevious = findViewById<Button>(R.id.btnPreviousAk)
        btnNext = findViewById<Button>(R.id.btnNextAk)
        btnSubmit = findViewById<Button>(R.id.btnSubmitAk)
        questionImageView = findViewById(R.id.question_ImageAk)
        val md = intent.getStringExtra("mode")
        if(md=="update"){
            mode=0
        }
        else{
            mode=1
        }
//        setupQuiz()
        setUpFirestore()
        setUpEventListener()
    }

    private fun setupQuiz() {
        val quizData = intent.getStringExtra("QUIZ")
        val quiz = Gson().fromJson<Quiz>(quizData, Quiz::class.java)
        quizzes = mutableListOf(quiz)
        questions = quizzes!![0].questions
        optionSelectorList = MutableList(questions!!.size) { OptionSelector() }
    }

    private fun setUpEventListener() {

        btnPrevious.setOnClickListener {
            index--
            bindViews()
        }

        btnNext.setOnClickListener {
            index++
            bindViews()
        }

        btnSubmit.setOnClickListener {
            if(mode==1){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
                return@setOnClickListener
            }else{
                updateAnswerKey()
                calculateScore()
                addAnswerKeyToFirebase(quizzes!![0])
            }
        }
    }
    private fun addAnswerKeyToFirebase(quiz : Quiz) {
        val collectionRef = FirebaseFirestore.getInstance().collection("quizzes")
        collectionRef.document(quiz.id).set(quiz)
            .addOnSuccessListener {
                Toast.makeText(this,"answer key updated successfully",Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()

            }.addOnFailureListener {
                Toast.makeText(this,"Some Error Occurred",Toast.LENGTH_SHORT).show()
            }
    }
    private fun calculateScore() {
        var score = 0
        for (entry in quizzes!![0].questions.entries) {
            val question = entry.value
            if (question.answer == question.userAnswer) {
                score += quizzes!![0].marksPerQuestion
            }
        }
//        val txtScore = findViewById<TextView>(R.id.test_score)
//        txtScore.text = "Your Score : $score"
        quizzes!![0].score=score
    }
    private fun updateAnswerKey() {
        var index = 1
        while(index <= questions!!.size){
            questions!!["$index"]!!.answer= optionSelectorList[index-1].userAnswer
            index=index+1
        }
        quizzes!![0].isKeyAvailable=true
    }
    private fun bindViews() {
        //yet to implement firebase database and delete this dummydatafunction
        btnPrevious.visibility = View.GONE
        btnNext.visibility = View.GONE

        if(index == 1){ //first question
            btnNext.visibility = View.VISIBLE
        }
        else if(index == questions!!.size) { // last question
            btnPrevious.visibility = View.VISIBLE
        }
        else{ // Middle
            btnPrevious.visibility = View.VISIBLE
            btnNext.visibility = View.VISIBLE
        }
        val optionList = findViewById<RecyclerView>(R.id.optionListAk)
        if(questions!!.size==0){
            Toast.makeText(this,"No questions in the Test", Toast.LENGTH_SHORT).show()
            return
        }
        val question = questions!!["$index"]
        val optionSelector = optionSelectorList[index-1]
        question?.let {
//            setImageWithData(it.imageUrl,it.ytop,it.yend);
            loadBase64ImageWithUrl(it.imageUrl,it.ytop,it.yend)
            val optionAdapter = OptionAdapter(this, optionSelector,it,mode)
            optionList.layoutManager = LinearLayoutManager(this)
            optionList.adapter = optionAdapter
            optionList.setHasFixedSize(true)
        }
    }
    fun loadBase64ImageWithUrl(base64Image: String, ytop: Int, yend: Int) {
        val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        val croppedBitmap = Bitmap.createBitmap(
            bitmap,
            0, ytop,
            bitmap.width,
            yend - ytop
        )

        Glide.with(questionImageView)
            .load(croppedBitmap)
            .into(questionImageView)
    }
    private fun setImageWithData(imageUrl: String, ytop: Int, yend: Int) {
//        Glide.with(this)
//            .load(imageUrl)
//            .into(imageView)
//        return

//
        // Load the image using Glide
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    // Get the cropped portion of the image
                    val croppedBitmap = Bitmap.createBitmap(
                        resource,
                        0,          // x-coordinate of the top-left corner of the cropped area
                        ytop,       // y-coordinate of the top-left corner of the cropped area
                        resource.width, // width of the cropped area
                        yend - ytop // height of the cropped area
                    )

                    // Set the cropped bitmap to the ImageView
                    val croppedImageView = findViewById<ImageView>(R.id.question_ImageAk)
                    croppedImageView.setImageBitmap(croppedBitmap)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Not implemented
                }
            })
    }
    private fun setUpFirestore() {
        firestore = FirebaseFirestore.getInstance()
        var id = intent.getStringExtra("id")
        if (id != null) {
            firestore.collection("quizzes").whereEqualTo("id",id)
                .get()
                .addOnSuccessListener {
                    if(it != null && !it.isEmpty){
                        quizzes = it.toObjects(Quiz::class.java)
                        questions = quizzes!![0].questions
                        optionSelectorList = MutableList(questions!!.size) { OptionSelector() }
                        bindViews()
                    }
                }
        }
    }
}