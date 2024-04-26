package com.andrzejbrzezinski.rossmannproductlist.filmpackage.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.andrzejbrzezinski.rossmannproductlist.databinding.ActivityAddCommentBinding
import com.andrzejbrzezinski.rossmannproductlist.databinding.ActivityFilmsBinding
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels.CommentsViewModel
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels.SharedViewModel
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels.ViewStateComment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AddCommentFragment: Fragment() {
    private val viewModel : CommentsViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: ActivityAddCommentBinding? = null
    private val binding get() = _binding!!

    private val films_binding:ActivityFilmsBinding by lazy{
        ActivityFilmsBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = ActivityAddCommentBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val filmnumber = arguments?.getInt("VIDEO_NUMBER") ?: -1


        viewModel.loginViewState.observe(viewLifecycleOwner, Observer { state ->

            when (state) {
                is ViewStateComment.ShowCommentForm -> showCommentForm()
                is ViewStateComment.WaitingForComment -> waitingForComment()
                is ViewStateComment.CommentAddSuccess -> navigateToPreviousScreen(state.username)
                is ViewStateComment.Error -> showError(state.message)

            }
        })
        binding.buttonPublishComment.setOnClickListener {
            if(binding.editTextComment.text.isEmpty())
                binding.editTextComment.setError("Comment is required")
            else
            {
                viewModel.addComment(filmnumber,binding.editTextComment.text.toString())

            }
        }




    }
    private fun showCommentForm() {

    }

    private fun waitingForComment() {

    }

    private fun navigateToPreviousScreen(user: String) {
        //setResult(Activity.RESULT_OK, Intent())
        //finish()
        //LoginState.account=true
        sharedViewModel.triggerCommentsRefresh("comment")
        (activity as MainFilmsActivity).showFilmsFragment()

    }

    private fun showError(message: String) {
        Timber.w(message)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}