package com.truongthong.map4d.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.transition.MaterialFadeThrough
import com.truongthong.map4d.R
import com.truongthong.map4d.viewmodel.Map4DViewModel
import kotlinx.android.synthetic.main.fragment_route_location.*
import vn.map4d.map.annotations.MFMarker
import vn.map4d.map.annotations.MFMarkerOptions
import vn.map4d.map.annotations.MFPolyline
import vn.map4d.map.annotations.MFPolylineOptions
import vn.map4d.map.camera.MFCameraUpdateFactory
import vn.map4d.map.core.Map4D
import vn.map4d.map.core.OnMapReadyCallback
import vn.map4d.types.MFLocationCoordinate
import java.util.*

class RouteLocationFragment : Fragment(), OnMapReadyCallback {

    private var currentMarker: MFMarker? = null
    private var map4D: Map4D? = null
    private lateinit var mapViewModel: Map4DViewModel
    private var polyline: MFPolyline? = null
    private var routeLocationList = MutableLiveData<MutableList<MFLocationCoordinate>>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mapViewRoute.onCreate(savedInstanceState)
        mapViewRoute.onResume()

        mapViewRoute.getMapAsync(this)

        mapViewModel = ViewModelProvider(this).get(Map4DViewModel::class.java)

//        edt_origin.inputType = 0
//        edt_destination.inputType = 0

    }

    override fun onMapReady(map: Map4D?) {
        map4D = map

        val latlong: MFLocationCoordinate = map4D?.cameraPosition!!.target

        mfUISeting()

        findRoute()

        drawMarker(latlong)

        buttonChooseVehicle()

        setOnMarkerDragListener()

    }

    private fun setOnMarkerDragListener() {
        map4D?.setOnMarkerDragListener(object : Map4D.OnMarkerDragListener {
            override fun onMarkerDrag(p0: MFMarker?) {
                btn_choose.visibility = View.VISIBLE
                if (polyline != null) {
                    polyline!!.remove()
                }
            }

            override fun onMarkerDragEnd(p0: MFMarker?) {
                if (currentMarker != null)
                    currentMarker?.remove()

                val newLatLng =
                    MFLocationCoordinate(p0?.position!!.latitude, p0.position!!.longitude)

                drawMarker(newLatLng)

                if (edt_origin.isFocused) {
                    setTextOrigin(newLatLng)
                }
                if (edt_destination.isFocused) {
                    setTextDestination(newLatLng)
                }

            }

            override fun onMarkerDragStart(p0: MFMarker?) {
            }

        })
    }

    @SuppressLint("SetTextI18n")
    private fun setTextOrigin(newLatLng: MFLocationCoordinate) {
        btn_choose.setOnClickListener {
            edt_origin.setText("${newLatLng.latitude},${newLatLng.longitude}").toString().trim()
            btn_choose.visibility = View.INVISIBLE
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setTextDestination(newLatLng: MFLocationCoordinate) {
        btn_choose.setOnClickListener {
            edt_destination.setText("${newLatLng.latitude},${newLatLng.longitude}").toString()
                .trim()
            btn_choose.visibility = View.INVISIBLE
        }

    }

    @SuppressLint("ResourceAsColor")
    private fun findRoute() {
        btn_start.setOnClickListener {
            mapViewModel.getRouteLocation(
                edt_origin.text.toString(),
                edt_destination.text.toString(),
                "motorcycle"
            )

            if (polyline != null)
                polyline!!.remove()
            polylineRoute()

            mapViewModel.routeLocation.observe(viewLifecycleOwner, {
                it.body()?.result?.let {
                    txt_distance.text = it.routes[0].distance.text
                    txt_duration.text = it.routes[0].duration.text
                    txt_motor.setTextColor(R.color.red_300)
                    txt_walk.setTextColor(R.color.black)
                    txt_car.setTextColor(R.color.black)
                }
            })

        }

    }

    @SuppressLint("ResourceAsColor")
    private fun buttonChooseVehicle() {

        cardView_Car.setOnClickListener {
            mapViewModel.getRouteLocation(
                edt_origin.text.toString(),
                edt_destination.text.toString(),
                "car"
            )

            mapViewModel.routeLocation.observe(viewLifecycleOwner, {
                it.body()?.result?.let {
                    txt_distance.text = it.routes[0].distance.text
                    txt_duration.text = it.routes[0].duration.text
                    txt_car.setTextColor(R.color.red_300)
                    txt_walk.setTextColor(R.color.black)
                    txt_motor.setTextColor(R.color.black)
                }
            })
        }

        cardView_Motor.setOnClickListener {
            mapViewModel.getRouteLocation(
                edt_origin.text.toString(),
                edt_destination.text.toString(),
                "motorcycle"
            )

            mapViewModel.routeLocation.observe(viewLifecycleOwner, {
                it.body()?.result?.let {
                    txt_distance.text = it.routes[0].distance.text
                    txt_duration.text = it.routes[0].duration.text
                    txt_motor.setTextColor(R.color.red_300)
                    txt_walk.setTextColor(R.color.black)
                    txt_car.setTextColor(R.color.black)
                }
            })

        }

        cardView_Walk.setOnClickListener {
            mapViewModel.getRouteLocation(
                edt_origin.text.toString(),
                edt_destination.text.toString(),
                "foot"
            )
            if (polyline != null)
                polyline!!.remove()
            polylineRoute()

            mapViewModel.routeLocation.observe(viewLifecycleOwner, {
                it.body()?.result?.let {
                    txt_distance.text = it.routes[0].distance.text
                    txt_duration.text = it.routes[0].duration.text
                    txt_walk.setTextColor(R.color.red_300)
                    txt_motor.setTextColor(R.color.black)
                    txt_car.setTextColor(R.color.black)
                }
            })
        }
    }

    private fun drawMarker(latlong: MFLocationCoordinate) {
        val markerOptions = MFMarkerOptions()
            .position(latlong)
            .snippet(getAddress(latlong.latitude, latlong.longitude))
        map4D?.animateCamera(MFCameraUpdateFactory.newCoordinate(latlong))
        map4D?.animateCamera(MFCameraUpdateFactory.newCoordinateZoom(latlong, 17.0))
        currentMarker = map4D?.addMarker(markerOptions)
        currentMarker?.isDraggable = true
        currentMarker?.showInfoWindow()

    }

    private fun getAddress(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val address = geocoder.getFromLocation(latitude, longitude, 1)
        return address[0].getAddressLine(0).toString()
    }

    private fun polylineRoute() {
        val latLngList = mutableListOf<MFLocationCoordinate>()

        mapViewModel.routeLocation.observe(viewLifecycleOwner, {
            it.body()?.result?.routes?.forEach {
                it.legs.forEach {
                    it.steps.forEach {
                        val mfLocationStart =
                            MFLocationCoordinate(it.startLocation.lat, it.startLocation.lng)
                        val mfLocationEnd =
                            MFLocationCoordinate(it.endLocation.lat, it.endLocation.lng)

                        latLngList.add(mfLocationStart)
                        latLngList.add(mfLocationEnd)
                    }
                }
            }
        })

        routeLocationList.value = latLngList

        polyline = map4D!!.addPolyline(
            MFPolylineOptions().add(*latLngList.toTypedArray())
                .color(Color.RED)
                .width(8f)
                .zIndex(10f)
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_route_location, container, false)

        enterTransition = MaterialFadeThrough()

        return view
    }

    private fun mfUISeting() {
        map4D?.uiSettings?.isMyLocationButtonEnabled = true
        map4D?.uiSettings?.isRotateGesturesEnabled = true
        map4D?.uiSettings?.setAllGesturesEnabled(true)
        map4D?.setTiltGesturesEnabled(true)

    }


}
