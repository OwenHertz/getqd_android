package com.app.upincode.getqd.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.app.upincode.getqd.GlobalClass;
import com.app.upincode.getqd.R;
import com.app.upincode.getqd.activities.inputs.GenericArrayAdapter;
import com.app.upincode.getqd.activities.inputs.NoDefaultSpinnerAdapter;
import com.app.upincode.getqd.activities.inputs.NoDefaultSpinnerItemSelectedAdapter;
import com.app.upincode.getqd.databinding.ActivitySelectTicketsBinding;
import com.app.upincode.getqd.databinding.SellTicketsInventoryItemBinding;
import com.app.upincode.getqd.enums.PaymentType;
import com.app.upincode.getqd.enums.ShippingType;
import com.app.upincode.getqd.errors.GQVolleyErrorHandler;
import com.app.upincode.getqd.networking.GQNetworkQueue;
import com.app.upincode.getqd.networking.GQNetworkUtils;
import com.app.upincode.getqd.networking.parsers.box_office.BOEventAccessParser;
import com.app.upincode.getqd.networking.parsers.box_office.BORequestTicketParser;
import com.app.upincode.getqd.networking.parsers.box_office.BOTicketBasketParser;
import com.app.upincode.getqd.networking.requests.box_office.BOCreateTicketBasketRequest;
import com.app.upincode.getqd.networking.requests.box_office.BORequestTicketsRequest;
import com.app.upincode.getqd.networking.requests.box_office.BORetrieveEventAccessRequest;
import com.app.upincode.getqd.networking.requests.box_office.BOUpdateTicketBasketRequest;
import com.app.upincode.getqd.utils.IntegerUtils;

import java.util.Arrays;
import java.util.List;


public class GQSelectTicketActivity extends GQBaseActivity {
    public static final String EVENT_ACCESS = "event_access";

    Spinner spinnerPayWith;
    Spinner spinnerDeliveryMethod;
    ListView listViewStaff;

    List<PaymentType> paymentTypes;
    List<ShippingType> shippingTypes;
    BOEventAccessParser eventAccess;
    BOTicketBasketParser basket;
    ActivitySelectTicketsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_tickets);

        //Retrieve selected venue sent via JSON
        Intent intent = getIntent();
        eventAccess = BOEventAccessParser.fromString(BOEventAccessParser.class, intent.getStringExtra(EVENT_ACCESS));


        this.setTitle("Sell Tickets - " + eventAccess.event.name);
        initBackButtonToolbar();

        spinnerPayWith = (Spinner) findViewById(com.app.upincode.getqd.R.id.PayWith);
        spinnerDeliveryMethod = (Spinner) findViewById(com.app.upincode.getqd.R.id.Delivery_Method);
        listViewStaff = (ListView) findViewById(com.app.upincode.getqd.R.id.listViewStaff);

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        globalVariable.setBasketInfo(null);  // if we had a basket before forget it.

        Button btyes2 = (Button) findViewById(com.app.upincode.getqd.R.id.ContinueButton);
        btyes2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateThenContinueCheckout();
            }
        });

        // Update basket's payment type when selected
        spinnerPayWith.setOnItemSelectedListener(new NoDefaultSpinnerItemSelectedAdapter() {
            @Override
            public void onRealItemSelected(AdapterView<?> parent, View view, int position, long id) {
                basket.payment_type = paymentTypes.get(position).id;
                basket.notifyChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                basket.payment_type = null;
                basket.notifyChange();
            }
        });

        // Update basket's shipping type when selected
        spinnerDeliveryMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (shippingTypes != null) {
                    basket.shipping_type = shippingTypes.get(position).id;
                    basket.notifyChange();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                basket.shipping_type = null;
                basket.notifyChange();
            }
        });

        beginCreateTicketBasket();
        beginRetrieveEventAccess();
    }

    public void beginCreateTicketBasket() {
        //Create ticket basket
        BOCreateTicketBasketRequest request = new BOCreateTicketBasketRequest(
                GQNetworkUtils.getRequestHeaders(this),
                new Response.Listener<BOTicketBasketParser>() {
                    @Override
                    public void onResponse(BOTicketBasketParser json) {
                        updateTicketBasket(json);
                        populatePaymentTypeSpinner();
                        populateShippingTypeSpinner();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Failure!
                        new GQVolleyErrorHandler(error).handle(getApplicationContext());
                    }
                });
        GQNetworkQueue.getInstance(this).addToRequestQueue(request);
    }

    public void updateTicketBasket(BOTicketBasketParser basket) {
        binding.setBasket(basket);
        this.basket = basket;
    }

    public void beginRetrieveEventAccess() {
        //Load event access detail to get inventory information
        BORetrieveEventAccessRequest request = new BORetrieveEventAccessRequest(
                eventAccess.slug, GQNetworkUtils.getRequestHeaders(this),
                new Response.Listener<BOEventAccessParser>() {
                    @Override
                    public void onResponse(BOEventAccessParser json) {
                        updateEventAccess(json);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Failure!
                        new GQVolleyErrorHandler(error).handle(getApplicationContext());
                    }
                });
        GQNetworkQueue.getInstance(this).addToRequestQueue(request);
    }

    public void updateEventAccess(BOEventAccessParser eventAccess) {
        binding.setEventAccess(eventAccess);
        this.eventAccess = eventAccess;

        ListAdapter adapter = new InventoryListAdapter(this, Arrays.asList(eventAccess.inventories));
        listViewStaff.setAdapter(adapter);
    }

    public void populatePaymentTypeSpinner() {
        paymentTypes = Arrays.asList(PaymentType.CASH, PaymentType.CREDIT, PaymentType.INTERAC, PaymentType.COMPLIMENTARY);
        SpinnerAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentTypes);
        adapter = new NoDefaultSpinnerAdapter(adapter, R.layout.spinner_default_payment_type, this);
        spinnerPayWith.setAdapter(adapter);
    }

    public void populateShippingTypeSpinner() {
        shippingTypes = Arrays.asList(ShippingType.PRINT_AT_HOME);
        SpinnerAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, shippingTypes);
        spinnerDeliveryMethod.setAdapter(adapter);
    }

    public void onInventoryQuantitySelected(BOEventAccessParser.InventoryParser inventory, Integer quantity) {
        int change = quantity - basket.getTicketCount(inventory);
        BORequestTicketParser requestParams = new BORequestTicketParser(change, inventory.ticket_type.id);

        //Request tickets
        BORequestTicketsRequest request = new BORequestTicketsRequest(
                basket.id, requestParams, GQNetworkUtils.getRequestHeaders(this),
                new Response.Listener<BOTicketBasketParser>() {
                    @Override
                    public void onResponse(BOTicketBasketParser json) {
                        updateTicketBasket(json);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Failure!
                        new GQVolleyErrorHandler(error).handle(GQSelectTicketActivity.this);
                    }
                });
        GQNetworkQueue.getInstance(this).addToRequestQueue(request);
    }

    public void updateThenContinueCheckout() {
        //Update ticket basket
        BOUpdateTicketBasketRequest request = new BOUpdateTicketBasketRequest(
                basket, GQNetworkUtils.getRequestHeaders(this),
                new Response.Listener<BOTicketBasketParser>() {
                    @Override
                    public void onResponse(BOTicketBasketParser json) {
                        updateTicketBasket(json);
                        startCheckoutActivity();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Failure!
                        new GQVolleyErrorHandler(error).handle(getApplicationContext());
                    }
                });
        GQNetworkQueue.getInstance(this).addToRequestQueue(request);
    }

    public void startCheckoutActivity() {
        Class cls = null;

        int type = basket.payment_type;
        if (type == PaymentType.INTERAC.id) {
            cls = GQInteracActivity.class;
        }
        if (type == PaymentType.CREDIT.id) {
            cls = GQCardioActivity.class;
        }
        if (type == PaymentType.CASH.id) {
            GQActivityUtils.showToast(this, "Using Cash to pay");
        }
        if (type == 4) {
            cls = GQCompActivity.class;
        }

        if (cls != null) {
            Intent intent = new Intent(this, cls);
            intent.putExtra(GQBaseMyTicketsCheckoutActivity.BASKET, basket.toString());
            intent.putExtra(GQBaseMyTicketsCheckoutActivity.EVENT_ACCESS, eventAccess.toString());
            startActivity(intent);
        }
        else {
            GQActivityUtils.showToast(this, "Please select payment type!");
        }
    }

    class InventoryListAdapter extends GenericArrayAdapter<BOEventAccessParser.InventoryParser> {
        GQSelectTicketActivity activity;
        Spinner spinnerQuantity;
        List<Integer> quantities;
        boolean isInitial = true;

        public InventoryListAdapter(GQSelectTicketActivity activity, List<BOEventAccessParser.InventoryParser> objects) {
            super(activity, objects);
            this.activity = activity;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final BOEventAccessParser.InventoryParser inventory = getItem(position);

            if (view == null) {
                SellTicketsInventoryItemBinding binding = SellTicketsInventoryItemBinding.inflate(getLayoutInflater());
                binding.setInventory(inventory);

                view = binding.getRoot();
            }

            quantities = Arrays.asList(IntegerUtils.getRange(0, inventory.ticket_type.purchase_limit + 1));

            spinnerQuantity = (Spinner) view.findViewById(R.id.spinnerQuantity);
            spinnerQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //Skip initial selection, as it does nothing
                    if (position == 0 && !isInitial) {
                        isInitial = false;
                        return;
                    }

                    activity.onInventoryQuantitySelected(inventory, quantities.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    activity.onInventoryQuantitySelected(inventory, 0);
                }
            });

            //Populate spinner
            SpinnerAdapter adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, quantities);
            spinnerQuantity.setAdapter(adapter);

            return view;
        }
    }
}
