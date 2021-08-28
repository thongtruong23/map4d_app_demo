package com.truongthong.map4d.ui

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.transition.MaterialFadeThrough
import com.truongthong.map4d.R
import com.truongthong.map4d.viewmodel.RouteViewModel
import kotlinx.android.synthetic.main.fragment_route_location.*
import kotlinx.android.synthetic.main.fragment_route_location.btn_mode_2D
import kotlinx.android.synthetic.main.fragment_route_location.btn_mode_3D
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
    private lateinit var routeViewModel : RouteViewModel
    private var polyline: MFPolyline? = null
    private var routeLocationList = MutableLiveData<MutableList<MFLocationCoordinate>>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mapViewRoute.onCreate(savedInstanceState)
        mapViewRoute.onResume()

        mapViewRoute.getMapAsync(this)

        routeViewModel = ViewModelProvider(this).get(RouteViewModel::class.java)

        btn_done.visibility = View.INVISIBLE

        btn_start.visibility = View.GONE

    }

    override fun onMapReady(map: Map4D?) {
        map4D = map

        val latlong: MFLocationCoordinate = map4D?.cameraPosition!!.target

        findRouteMode()
        drawMarker(latlong)
        setOnMarkerDragListener()
        processDataLocation()
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

    private fun setOnMarkerDragListener() {
        map4D?.setOnMarkerDragListener(object : Map4D.OnMarkerDragListener {
            override fun onMarkerDrag(p0: MFMarker?) {
                btn_choose.visibility = View.VISIBLE
                txt_notice.visibility = View.GONE
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
            btn_done.visibility = View.VISIBLE
        }

    }

    private fun processDataLocation() {
        btn_done.setOnClickListener {
            routeViewModel.getRouteLocation(
                edt_origin.text.toString(),
                edt_destination.text.toString(),
                "motorcycle"
            )

            btn_done.visibility = View.INVISIBLE
            btn_start.visibility = View.VISIBLE
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun findRouteMode() {
        btn_start.setOnClickListener {

            polylineRoute()

            btn_start.visibility = View.GONE

            routeViewModel.routeLocation.observe(viewLifecycleOwner, {
                it.body()?.result?.let {
                    txt_distance.text = it.routes[0].distance.text
                    txt_duration.text = it.routes[0].duration.text
                    txt_motor.setTextColor(R.color.red_300)
                    txt_walk.setTextColor(R.color.black)
                    txt_car.setTextColor(R.color.black)
                }
            })

        }

        cardView_Car.setOnClickListener {
            routeViewModel.getRouteLocation(
                edt_origin.text.toString(),
                edt_destination.text.toString(),
                "car"
            )
            if (polyline != null)
                polyline!!.remove()
            polylineRoute()

            routeViewModel.routeLocation.observe(viewLifecycleOwner, {
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
            routeViewModel.getRouteLocation(
                edt_origin.text.toString(),
                edt_destination.text.toString(),
                "motorcycle"
            )
            if (polyline != null)
                polyline!!.remove()
            polylineRoute()

            routeViewModel.routeLocation.observe(viewLifecycleOwner, {
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
            routeViewModel.getRouteLocation(
                edt_origin.text.toString(),
                edt_destination.text.toString(),
                "foot"
            )
            if (polyline != null)
                polyline!!.remove()
            polylineRoute()

            routeViewModel.routeLocation.observe(viewLifecycleOwner, {
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

        routeViewModel.routeLocation.observe(viewLifecycleOwner, {
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

            routeLocationList.value = latLngList

        })

        polyline = map4D?.addPolyline(
            MFPolylineOptions().add(*latLngList.toTypedArray())
                .color(ContextCompat.getColor(context ?: return, R.color.red))
                .width(8.0f)
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

}
