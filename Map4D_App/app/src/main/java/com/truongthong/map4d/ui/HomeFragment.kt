package com.truongthong.map4d.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialFadeThrough
import com.truongthong.map4d.R
import com.truongthong.map4d.adapter.MapSearchAdapter
import com.truongthong.map4d.model.search.MapLocation
import com.truongthong.map4d.viewmodel.Map4DViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.btn_mode_2D
import kotlinx.android.synthetic.main.fragment_home.btn_mode_3D
import kotlinx.android.synthetic.main.fragment_home.btn_route
import kotlinx.android.synthetic.main.fragment_restaurant.*
import vn.map4d.map.annotations.MFMarkerOptions
import vn.map4d.map.camera.MFCameraUpdateFactory
import vn.map4d.map.core.Map4D
import vn.map4d.map.core.OnMapReadyCallback
import vn.map4d.types.MFLocationCoordinate


class HomeFragment : Fragment(), OnMapReadyCallback, MapSearchAdapter.OnMapItemClickListener {

    private var map4D: Map4D? = null
    private var mapAdapter = MapSearchAdapter(arrayListOf(), this)
    private lateinit var mapViewModel: Map4DViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        permissionMap()

        mapView.getMapAsync(this)

        setupRecyclerView()

        clickRouteListener()

        clickButtonNearbyPlace()

        mapViewModel = ViewModelProvider(this).get(Map4DViewModel::class.java)

        mapViewModel.searchLocation.observe(viewLifecycleOwner, { reponse ->
            if (reponse.isSuccessful) {
                reponse.body()?.let { locationRespone ->
                    mapAdapter.setLocationData(locationRespone.result)

                }

            }

        })

        search_bar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                btn_route.visibility = View.INVISIBLE
                btn_add_location.visibility = View.INVISIBLE
                map4D?.uiSettings?.isMyLocationButtonEnabled = false
                rv_listSuggest.visibility = View.VISIBLE

                if (query.toString().trim().isNotBlank() && query != null) {
                    mapViewModel.getSearchLocation(query)
                    mapAdapter.filter.filter(query)
                } else {
//                    Toast.makeText(requireContext(),"is Null", Toast.LENGTH_SHORT).show()
                    rv_listSuggest.visibility = View.INVISIBLE
                    btn_route.visibility = View.VISIBLE
                    btn_add_location.visibility = View.VISIBLE
                    map4D?.uiSettings?.isMyLocationButtonEnabled = true
                }
                return false
            }
        })
    }


    private fun clickRouteListener() {
        btn_route.setOnClickListener {
            val routeLocationFragment: Fragment = RouteLocationFragment()
            val transaction: FragmentTransaction = fragmentManager?.beginTransaction()!!

            transaction.replace(R.id.frameLayout, routeLocationFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    private fun clickButtonNearbyPlace() {
        btn_restaurant.setOnClickListener {
            val restaurantFragment: Fragment = RestaurantFragment()
            val transaction: FragmentTransaction = fragmentManager?.beginTransaction()!!

            transaction.replace(R.id.frameLayout, restaurantFragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }
        btn_atm.setOnClickListener {
            val atmFragment: Fragment = ATMFragment()
            val transaction: FragmentTransaction = fragmentManager?.beginTransaction()!!

            transaction.replace(R.id.frameLayout, atmFragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }
        btn_cafe.setOnClickListener {
            val coffeeFragment: Fragment = CoffeeFragment()
            val transaction: FragmentTransaction = fragmentManager?.beginTransaction()!!

            transaction.replace(R.id.frameLayout, coffeeFragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }
        btn_parking.setOnClickListener {
            val parkingFragment: Fragment = ParkingFragment()
            val transaction: FragmentTransaction = fragmentManager?.beginTransaction()!!

            transaction.replace(R.id.frameLayout, parkingFragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }
        btn_school.setOnClickListener {
            val schoolFragment: Fragment = SchoolFragment()
            val transaction: FragmentTransaction = fragmentManager?.beginTransaction()!!

            transaction.replace(R.id.frameLayout, schoolFragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }
    }

    private fun setupRecyclerView() {
        mapAdapter = MapSearchAdapter(arrayListOf(), this)
        rv_listSuggest.apply {
            adapter = mapAdapter
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
        }
    }

    override fun onMapReady(map: Map4D?) {
        map4D = map
        getCurrentLocation()
        mfUISeting()

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
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        enterTransition = MaterialFadeThrough()
        return view
    }

    fun getCurrentLocation() {
        val location: MFLocationCoordinate = map4D?.cameraPosition!!.target
        map4D?.addMarker(
            MFMarkerOptions().position(location).title("83 Phan Thanh")
                .snippet("Phường Phước Ninh, Quận Hải Châu, Thành Phố Đà Nẵng")
        )
        map4D?.moveCamera(MFCameraUpdateFactory.newCoordinate(location))
    }

    //cap quyen su dung
    fun permissionMap() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 100
            )
        }

    }

    fun mfUISeting() {
        map4D?.uiSettings?.isMyLocationButtonEnabled = true
        map4D?.uiSettings?.isRotateGesturesEnabled = true
        map4D?.uiSettings?.setAllGesturesEnabled(true)
        map4D?.setTiltGesturesEnabled(true)

    }

    //xu ly su kien khi click vao item recyclerview
    override fun onItemClick(suggestList: MapLocation, position: Int) {

        val lat = suggestList.location.lat
        val lng = suggestList.location.lng

        val mLocation = MFLocationCoordinate(lat, lng)

        map4D?.addMarker(
            MFMarkerOptions().position(mLocation).title(suggestList.name)
                .snippet(suggestList.address)
        )
//        map4D?.moveCamera(MFCameraUpdateFactory.newCoordinateZoom(mLocation,19.0))
        map4D?.animateCamera(MFCameraUpdateFactory.newCoordinate(mLocation))

        rv_listSuggest.visibility = View.INVISIBLE
        btn_route.visibility = View.VISIBLE
        btn_add_location.visibility = View.VISIBLE
        map4D?.uiSettings?.isMyLocationButtonEnabled = true

    }

}

