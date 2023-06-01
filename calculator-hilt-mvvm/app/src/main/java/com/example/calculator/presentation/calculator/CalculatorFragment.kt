package com.example.calculator.presentation.calculator

import android.os.Bundle
import android.view.*
import android.widget.HorizontalScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.calculator.R
import com.example.calculator.databinding.FragmentCalculatorBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A [Fragment] representing the app's main arithmetic calculator interface.
 */
@AndroidEntryPoint
class CalculatorFragment : Fragment() {

    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        setupAppBar()
        val binding: FragmentCalculatorBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_calculator, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setInitialExpression()
        setupOnLongClickListeners(binding, viewModel)
        setupViewModelObservers(binding, viewModel)
        return binding.root
    }
    private fun setInitialExpression() {
        val initialExpression = CalculatorFragmentArgs.fromBundle(
            requireArguments()).initialExpression

        viewModel.expression.postValue(initialExpression)
    }

    private fun setupOnLongClickListeners(
        binding: FragmentCalculatorBinding,
        viewModel: CalculatorViewModel
    ) {
        binding.deleteBtn.setOnLongClickListener {
            viewModel.onClear()
            true
        }
    }
    private fun setupAppBar() {
        val appBar = (activity as AppCompatActivity).supportActionBar
        appBar?.title = getString(R.string.title_calculator)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_calculator_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_history -> {
                findNavController().navigate(CalculatorFragmentDirections
                    .actionCalculatorFragmentToHistoryFragment())

                item.isVisible = false
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupViewModelObservers(
        binding: FragmentCalculatorBinding,
        viewModel: CalculatorViewModel
    ) {
        viewModel.expression.observe(viewLifecycleOwner) { expression ->
            viewLifecycleOwner.lifecycleScope.launch {
                expression?.let {
                    delay(50)

                    binding.outputScroll.scrollToRight()
                }
            }
        }

        viewModel.resultPreview.observe(viewLifecycleOwner) { resultPreview ->
            viewLifecycleOwner.lifecycleScope.launch {
                resultPreview?.let {
                    delay(50)

                    binding.resultScroll.scrollToRight()
                }
            }
        }
    }
    private fun HorizontalScrollView.scrollToRight() {
        val lastChild = getChildAt(childCount - 1)
        val right = lastChild.right + paddingRight
        val delta = right - (scrollX + width)
        smoothScrollBy(delta, 0)
    }
}