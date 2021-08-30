package com.truongthong.map4d.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialFadeThrough
import com.truongthong.map4d.R
import com.truongthong.map4d.adapter.SchoolAdapter
import com.truongthong.map4d.model.nearby.ResultNearby
import com.truongthong.map4d.viewmodel.NearbyPlaceViewModel
import kotlinx.android.synthetic.main.fragment_restaurant.btn_mode_2D
import kotlinx.android.synthetic.main.fragment_restaurant.btn_mode_3D
import kotlinx.android.synthetic.main.fragment_restaurant.rv_listNearby
import kotlinx.android.synthetic.main.fragment_restaurant.search_bar_res
import kotlinx.android.synthetic.main.fragment_school.*
import vn.map4d.map.annotations.MFMarker
import vn.map4d.map.annotations.MFMarkerOptions
import vn.map4d.map.camera.MFCameraUpdateFactory
import vn.map4d.map.core.Map4D
import vn.map4d.map.core.OnMapReadyCallback
import vn.map4d.types.MFLocationCoordinate


class SchoolFragment : Fragment(), OnMapReadyCallback,
    SchoolAdapter.OnMapItemClickListener {

    private var map4D: Map4D? = null
    private var currentMarker: MFMarker? = null
    private var nearbyAdapter = SchoolAdapter(arrayListOf(), this)
    private lateinit var nearbyPlaceViewModel: NearbyPlaceViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mapViewSchool.onCreate(savedInstanceState)
        mapViewSchool.onResume()

        mapViewSchool.getMapAsync(this)

        mapViewSchool.visibility = View.INVISIBLE

        setupRecyclerView()

        nearbyPlaceViewModel = ViewModelProvider(this).get(NearbyPlaceViewModel::class.java)

        nearbyPlaceViewModel.getNearby("16.0721,108.2243", 8000, "truong thcs")

        nearbyPlaceViewModel.getNearby.observe(viewLifecycleOwner, { reponse ->
            if (reponse.isSuccessful) {
                reponse.body()?.let { locationRespone ->
                    nearbyAdapter.setLocationData(locationRespone.result)

                }

            }

        })

        search_bar_res.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                mapViewSchool.visibility = View.INVISIBLE
                rv_listNearby.visibility = View.VISIBLE

                if (query.toString().trim().isNotBlank() && query != null) {
                    nearbyPlaceViewModel.getNearby("16.0721,108.2243", 8000, query)
                } else {
                    rv_listNearby.visibility = View.INVISIBLE
                    mapViewSchool.visibility = View.INVISIBLE
                }
                return false
            }
        })

    }

    private fun setupRecyclerView() {
        nearbyAdapter = SchoolAdapter(arrayListOf(), this)
        rv_listNearby.apply {
            adapter = nearbyAdapter
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
        }
    }

    override fun onMapReady(map: Map4D?) {
        map4D = map

        modeMap()

    }

    private fun modeMap() {
        btn_mode_3D.setOnClickListener {
            map4D?.enable3DMode(true)
            btn_mode_3D.visibility = View.INVISIBLE
            btn_mode_2D.visibility = View.VISIBLE
        }

        btn_mode_2D.setOnClickListener {
            map4D?.enable3DMode(false)
            btn_mode_2D.visibility = View.INVISIBLE
            btn_mode_3D.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_school, container, false)

        enterTransition = MaterialFadeThrough()
        return view
    }

    override fun onItemClick(schoolList: ResultNearby, position: Int) {
        mapViewSchool.visibility = View.VISIBLE

        if (currentMarker != null) {
            currentMarker!!.remove()

            val newLat = schoolList.location.lat
            val newLng = schoolList.location.lng
            val mNewLocation = MFLocationCoordinate(newLat, newLng)

            currentMarker = map4D?.addMarker(
                MFMarkerOptions().position(mNewLocation).title(schoolList.name)
                    .snippet(schoolList.address)
            )
            map4D?.animateCamera(MFCameraUpdateFactory.newCoordinate(mNewLocation))

        } else {
            val lat = schoolList.location.lat
            val lng = schoolList.location.lng
            val mLocation = MFLocationCoordinate(lat, lng)

            currentMarker = map4D?.addMarker(
                MFMarkerOptions().position(mLocation).title(schoolList.name)
                    .snippet(schoolList.address)
            )
            map4D?.animateCamera(MFCameraUpdateFactory.newCoordinate(mLocation))
        }

        rv_listNearby.visibility = View.INVISIBLE

    }

}

