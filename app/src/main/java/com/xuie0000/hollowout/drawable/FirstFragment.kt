package com.xuie0000.hollowout.drawable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import com.xuie0000.hollowout.drawable.databinding.FragmentFirstBinding
import timber.log.Timber

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

  private var _binding: FragmentFirstBinding? = null

  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {

    _binding = FragmentFirstBinding.inflate(inflater, container, false)
    return binding.root

  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.buttonFirst.setOnClickListener {
      findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }

    val url = "http://p1.music.126.net/wSMfGvFzOAYRU_yVIfquAA==/2946691248081599.jpg"
    Timber.d("url: $url")
    binding.image1.load(url) {
      allowHardware(false)
    }

    val request = ImageRequest.Builder(requireContext())
      .allowHardware(false)
      .data(url)
      .target(
        onSuccess = { result ->
          Timber.d("HollowOutImageView load image success")
          binding.image2.setImageDrawable(result)
          binding.image5.setImageDrawable(result)
          binding.image6.setImageDrawable(result)
        }
      )
      .build()
    requireContext().imageLoader.enqueue(request)

    binding.imageNet.load("http://p1.music.126.net/GcKk4tk_HqiYwKEA-_hk3w==/109951164406461029.jpg") {
      allowHardware(false)
    }

  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}