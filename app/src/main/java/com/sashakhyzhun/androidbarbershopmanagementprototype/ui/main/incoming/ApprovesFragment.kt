package com.sashakhyzhun.androidbarbershopmanagementprototype.ui.main.incoming

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener
import com.sashakhyzhun.androidbarbershopmanagementprototype.R
import com.sashakhyzhun.androidbarbershopmanagementprototype.data.PaperConst
import com.sashakhyzhun.androidbarbershopmanagementprototype.model.AcceptedRequest
import com.sashakhyzhun.androidbarbershopmanagementprototype.model.IncomingRequest
import io.paperdb.Paper

import java.util.ArrayList

/**
 * @author SashaKhyzhun
 * Created on 9/14/18.
 */
class ApprovesFragment : Fragment() {

    private lateinit var onTouchListener: RecyclerTouchListener

    private lateinit var tvIncomingRequests: TextView
    private lateinit var incoming: ArrayList<IncomingRequest>
    private lateinit var adapterIncoming: IncomingAdapter
    private lateinit var recyclerIncoming: RecyclerView

    private lateinit var tvAcceptedRequests: TextView
    private lateinit var accepted: ArrayList<AcceptedRequest>
    private lateinit var adapterAccepted: AcceptedAdapter
    private lateinit var recyclerAccepted: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        incoming = ArrayList()
        accepted = ArrayList()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_approves, container, false)

        tvIncomingRequests = view.findViewById(R.id.tvIncomingRequest)
        tvAcceptedRequests = view.findViewById(R.id.tvAcceptedRequests)

        val incomingList: MutableList<IncomingRequest> = Paper.book().read(PaperConst.incomingList, arrayListOf())
        val acceptedList: MutableList<AcceptedRequest> = Paper.book().read(PaperConst.acceptedList, arrayListOf())

        incoming.addAll(incomingList)
        accepted.addAll(acceptedList)

        updateTvAccepted(accepted)
        updateTvIncoming(incoming)

        loadIncomingAdapter(view)
        loadAcceptedAdapter(view)


        onTouchListener = RecyclerTouchListener(activity, recyclerIncoming)
        onTouchListener
                //.setIndependentViews(R.id.rowButton)
                //.setViewsToFade(R.id.rowButton)
                .setLongClickable(true) { position -> }
                .setSwipeOptionViews(R.id.layout_accept, R.id.layout_cancel)
                .setSwipeable(R.id.rowFG, R.id.rowBG) { viewID, position ->
                    when (viewID) {
                        R.id.layout_accept -> {
                            val req = incoming[position]
                            val newUser = AcceptedRequest(req.name, req.regDay, req.startHour, req.endHour)

                            // add new user to accepted list
                            acceptedList.add(newUser)
                            accepted.add(newUser)
                            Paper.book().write(PaperConst.acceptedList, accepted)

                            // remove user from incoming list
                            incomingList.removeAt(position)
                            incoming.removeAt(position)
                            Paper.book().write(PaperConst.incomingList, incoming)
                        }
                        R.id.layout_cancel -> {
                            // remove user from incoming
                            incomingList.removeAt(position)
                            incoming.removeAt(position)
                            Paper.book().write(PaperConst.incomingList, incoming)
                        }
                    }
                    // update list
                    updateRV()
                    updateTvIncoming(incoming)
                    updateTvAccepted(accepted)
                }

        return view
    }

    private fun loadIncomingAdapter(view: View) {
        adapterIncoming = IncomingAdapter(context!!, incoming)
        recyclerIncoming = view.findViewById(R.id.recyclerViewRequests)
        recyclerIncoming.adapter = adapterIncoming
        recyclerIncoming.layoutManager = LinearLayoutManager(context)
    }

    private fun loadAcceptedAdapter(view: View) {
        adapterAccepted = AcceptedAdapter(context!!, accepted)
        recyclerAccepted = view.findViewById(R.id.recyclerViewAccepted)
        recyclerAccepted.adapter = adapterAccepted
        recyclerAccepted.layoutManager = LinearLayoutManager(context)
    }

    private fun updateTvIncoming(list: ArrayList<IncomingRequest>) {
        tvIncomingRequests.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun updateTvAccepted(list: ArrayList<AcceptedRequest>) {
        tvAcceptedRequests.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun updateRV() {
        recyclerIncoming.adapter = adapterIncoming
        recyclerIncoming.layoutManager = LinearLayoutManager(context)

        recyclerAccepted.adapter = adapterAccepted
        recyclerAccepted.layoutManager = LinearLayoutManager(context)
    }

    override fun onResume() {
        super.onResume()
        recyclerIncoming.addOnItemTouchListener(onTouchListener)
    }

    override fun onPause() {
        super.onPause()
        recyclerIncoming.removeOnItemTouchListener(onTouchListener)
    }


}
