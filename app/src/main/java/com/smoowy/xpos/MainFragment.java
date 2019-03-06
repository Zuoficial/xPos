package com.smoowy.xpos;


import android.app.Dialog;
import android.arch.lifecycle.Lifecycle;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainFragment extends Fragment {


    public MainFragment() {
    }

    EditText etCantidad, etPorcentaje, etReferencia, etCantidadMostrador;
    TextView tTamano, tLote, tTamanoC, tLoteC,
            tTituloCantidad, tTituloPorcentaje, tTituloConversion, tTituloTamano, tMargen, tMargenC,
            tSeguro, tSeguroC;
    double cantidad, porcentajeEntero, referencia, tamanoPosicion,
            lote, tamanoPosicionC, loteC, margen, margenC, necesario, necesarioC,
            redondeoRef = 1000, ajusteRefRespaldo, resPrecioXDialogPos, resPorcentajeXDialogPos;
    int apalancamiento;
    int tipoApalancamiento = 4;
    boolean hayDecimales, yaRedondeo, resYaRedondeo, seAplanoLimpiar;
    ClipboardManager clipboard;
    Button bApalancamiento, bLimpiarClaro, bRedondeoDescendente,
            bRedondeoAscendente, bRegresarClaro, bPos;
    SharedPreferences sharedPreferences;
    String resCantidad, resCantidadMostrador, resPorcentaje, resReferencia;
    String resCantidadRedo, resPorcentajeRedo, resReferenciaRedo;
    InputMethodManager inputMethodManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        etCantidad = view.findViewById(R.id.et_cantidad);
        etCantidadMostrador = view.findViewById(R.id.et_cantidad_mostrador);
        etCantidadMostrador.addTextChangedListener(textWatcherMostrador);
        etPorcentaje = view.findViewById(R.id.et_porcentaje);
        etReferencia = view.findViewById(R.id.et_referencia);
        etCantidad.addTextChangedListener(textWatcher);
        etPorcentaje.addTextChangedListener(textWatcher);
        etReferencia.addTextChangedListener(textWatcher);
        tTamano = view.findViewById(R.id.t_posicion_tamano);
        tTamano.setOnClickListener(clickListenerTamano);
        tLote = view.findViewById(R.id.t_posicion_lote);
        tLote.setOnClickListener(clickListenerLotes);
        tTamanoC = view.findViewById(R.id.t_posicion_tamano_conversion);
        tTamanoC.setOnClickListener(clickListenerTamanoConversion);
        tLoteC = view.findViewById(R.id.t_posicion_lote_conversion);
        tLoteC.setOnClickListener(clickListenerLotesConversion);
        tTituloCantidad = view.findViewById(R.id.t_titulo_cant);
        tTituloPorcentaje = view.findViewById(R.id.t_titulo_porcen);
        tTituloConversion = view.findViewById(R.id.t_titulo_convers);
        tTituloTamano = view.findViewById(R.id.t_titulo_tamano);
        tTituloCantidad.setOnClickListener(onClickListener);
        tTituloPorcentaje.setOnClickListener(onClickListener);
        tTituloConversion.setOnClickListener(onClickListener);
        tTituloTamano.setOnClickListener(onClickListener);
        tSeguro = view.findViewById(R.id.t_seguro);
        tSeguroC = view.findViewById(R.id.t_seguroC);
        tMargen = view.findViewById(R.id.t_margen);
        tMargen.setOnClickListener(clickListenerMargen);
        tMargenC = view.findViewById(R.id.t_margenC);
        tMargenC.setOnClickListener(clickListenerMargenConversion);
        bApalancamiento = view.findViewById(R.id.b_apalancamiento);
        bApalancamiento.setOnClickListener(onClickListener);
        bApalancamiento.setOnLongClickListener(oLClickListenerApalancamiento);
        bLimpiarClaro = view.findViewById(R.id.b_limpiar_claro);
        bLimpiarClaro.setOnClickListener(onClickListener);
        bRedondeoDescendente = view.findViewById(R.id.b_redondeo_descendente);
        bRedondeoDescendente.setOnClickListener(onClickListener);
        bRedondeoDescendente.setOnLongClickListener(oLClickListenerRedondeoDescendente);
        bRedondeoAscendente = view.findViewById(R.id.b_redondeo_ascendente);
        bRedondeoAscendente.setOnClickListener(onClickListener);
        bRedondeoAscendente.setOnLongClickListener(oLClickListenerRedondeoAscendente);
        bRegresarClaro = view.findViewById(R.id.b_regresar_claro);
        bRegresarClaro.setOnClickListener(onClickListener);
        bPos = view.findViewById(R.id.b_pos);
        bPos.setOnClickListener(onClickListener);
        checadaSharedPreference();
        cambioApalancamiento();
        return view;

    }

    Dialog dialog;
    EditText etDialogReferencia;
    TextView tDialogTitulo;

    private void crearDialogRedondeo(boolean esAscendente) {
        dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        dialog.setContentView(R.layout.dialog);
        dialog.show();
        etDialogReferencia = dialog.findViewById(R.id.et_dialog_ref);
        tDialogTitulo = dialog.findViewById(R.id.t_dialog_titulo);
        tDialogTitulo.setText("Redondeo");
        if (redondeoRef % 1 == 0)
            etDialogReferencia.setText(String.format("%.0f", redondeoRef));
        else
            etDialogReferencia.setText(String.valueOf(redondeoRef));

        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        etDialogReferencia.setOnKeyListener((view, i, keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                redondeoRef = Double.parseDouble(etDialogReferencia.getText().toString());
                if (esAscendente)
                    ajusteRedondeo(true);
                else
                    ajusteRedondeo(false);
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
                return true;

            } else
                return false;
        });

    }

    private void crearDialogApalancamiento() {
        dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        dialog.setContentView(R.layout.dialog);
        dialog.show();
        etDialogReferencia = dialog.findViewById(R.id.et_dialog_ref);
        etDialogReferencia.setText(String.valueOf(apalancamiento));
        tDialogTitulo = dialog.findViewById(R.id.t_dialog_titulo);
        tDialogTitulo.setText("Leverage");
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        etDialogReferencia.setOnKeyListener((view, i, keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                apalancamiento = (int) Double.parseDouble(etDialogReferencia.getText().toString());
                tipoApalancamiento = 0;
                cambioApalancamiento();
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
                return true;

            } else
                return false;
        });
    }


    private void crearDialogTamano() {
        dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        dialog.setContentView(R.layout.dialog);
        dialog.show();
        etDialogReferencia = dialog.findViewById(R.id.et_dialog_ref);
        if (hayDecimales)
            etDialogReferencia.setText(String.format("%.2f", tamanoPosicion));
        else
            etDialogReferencia.setText(String.format("%.0f", tamanoPosicion));
        tDialogTitulo = dialog.findViewById(R.id.t_dialog_titulo);
        tDialogTitulo.setText("TP");
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        etDialogReferencia.setOnKeyListener((view, i, keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                respaldoDeET();
                double num = Double.parseDouble(etDialogReferencia.getText().toString());
                num *= porcentajeEntero / 100;

                if (num % 1 > 0) {
                    etCantidadMostrador.setText(String.format("%.2f", num));
                    etCantidad.setText(String.format("%.4f", num));

                } else {
                    etCantidadMostrador.setText(String.format("%.0f", num));
                }

                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
                return true;

            } else
                return false;
        });
    }


    private void crearDialogTamanoConversion() {
        dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        dialog.setContentView(R.layout.dialog);
        dialog.show();
        etDialogReferencia = dialog.findViewById(R.id.et_dialog_ref);
        if (hayDecimales)
            etDialogReferencia.setText(String.format("%.2f", tamanoPosicionC));
        else
            etDialogReferencia.setText(String.format("%.0f", tamanoPosicionC));
        tDialogTitulo = dialog.findViewById(R.id.t_dialog_titulo);
        tDialogTitulo.setText("TPC");
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        etDialogReferencia.setOnKeyListener((view, i, keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                respaldoDeET();
                double num = Double.parseDouble(etDialogReferencia.getText().toString());
                num *= referencia;
                num *= porcentajeEntero / 100;

                if (num % 1 > 0) {
                    etCantidadMostrador.setText(String.format("%.2f", num));
                    etCantidad.setText(String.format("%.4f", num));

                } else {
                    etCantidadMostrador.setText(String.format("%.0f", num));
                }

                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
                return true;

            } else
                return false;
        });
    }


    private void crearDialogLotes() {
        dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        dialog.setContentView(R.layout.dialog);
        dialog.show();
        etDialogReferencia = dialog.findViewById(R.id.et_dialog_ref);
        etDialogReferencia.setText(String.format("%.4f", tamanoPosicion / 100000));
        tDialogTitulo = dialog.findViewById(R.id.t_dialog_titulo);
        tDialogTitulo.setText("Lotes");
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        etDialogReferencia.setOnKeyListener((view, i, keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                respaldoDeET();
                double num = Double.parseDouble(etDialogReferencia.getText().toString());
                num *= 100000;
                num *= porcentajeEntero / 100;

                if (num % 1 > 0) {
                    etCantidadMostrador.setText(String.format("%.2f", num));
                    etCantidad.setText(String.format("%.4f", num));

                } else {
                    etCantidadMostrador.setText(String.format("%.0f", num));
                }

                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
                return true;

            } else
                return false;
        });
    }

    private void crearDialogLotesConversion() {
        dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        dialog.setContentView(R.layout.dialog);
        dialog.show();
        etDialogReferencia = dialog.findViewById(R.id.et_dialog_ref);
        etDialogReferencia.setText(String.format("%.4f", tamanoPosicionC / 100000));
        tDialogTitulo = dialog.findViewById(R.id.t_dialog_titulo);
        tDialogTitulo.setText("LC");
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        etDialogReferencia.setOnKeyListener((view, i, keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                respaldoDeET();
                double num = Double.parseDouble(etDialogReferencia.getText().toString());
                num *= 100000;
                num *= referencia;
                num *= porcentajeEntero / 100;

                if (num % 1 > 0) {
                    etCantidadMostrador.setText(String.format("%.2f", num));
                    etCantidad.setText(String.format("%.4f", num));

                } else {
                    etCantidadMostrador.setText(String.format("%.0f", num));
                }

                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
                return true;

            } else
                return false;
        });
    }


    boolean esCantidadEnDialogMargen = true;

    private void crearDialogMargen() {
        dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        dialog.setContentView(R.layout.dialog);
        dialog.show();
        etDialogReferencia = dialog.findViewById(R.id.et_dialog_ref);
        etDialogReferencia.setText(String.format("%.2f", margen));
        tDialogTitulo = dialog.findViewById(R.id.t_dialog_titulo);
        if (esCantidadEnDialogMargen) {
            tDialogTitulo.setText("Margen");
        } else {
            tDialogTitulo.setText("Margen %");
        }
        tDialogTitulo.setOnClickListener(view -> {
            esCantidadEnDialogMargen = !esCantidadEnDialogMargen;
            if (esCantidadEnDialogMargen) {
                tDialogTitulo.setText("Margen");
            } else {
                tDialogTitulo.setText("Margen %");
            }
        });
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        etDialogReferencia.setOnKeyListener((view, i, keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                respaldoDeET();
                double num = Double.parseDouble(etDialogReferencia.getText().toString());
                num *= apalancamiento;

                if (esCantidadEnDialogMargen) {

                    num *= porcentajeEntero / 100;

                    if (num % 1 > 0) {
                        etCantidadMostrador.setText(String.format("%.2f", num));
                        etCantidad.setText(String.format("%.4f", num));

                    } else {
                        etCantidadMostrador.setText(String.format("%.0f", num));
                    }

                } else {
                    double porcEntero = cantidad / num;
                    porcEntero *= 100;

                    if (porcEntero % 1 > 0) {
                        etPorcentaje.setText(String.format("%.4f", porcEntero));

                    } else {
                        etPorcentaje.setText(String.format("%.0f", porcEntero));
                    }
                }

                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
                return true;

            } else
                return false;
        });
    }


    private void crearDialogTamanoMargenConversion() {
        dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        dialog.setContentView(R.layout.dialog);
        dialog.show();
        etDialogReferencia = dialog.findViewById(R.id.et_dialog_ref);
        etDialogReferencia.setText(String.format("%.2f", margenC));
        tDialogTitulo = dialog.findViewById(R.id.t_dialog_titulo);
        if (esCantidadEnDialogMargen) {
            tDialogTitulo.setText("MC");
        } else {
            tDialogTitulo.setText("MC %");
        }

        tDialogTitulo.setOnClickListener(view -> {
            esCantidadEnDialogMargen = !esCantidadEnDialogMargen;
            if (esCantidadEnDialogMargen) {
                tDialogTitulo.setText("MC");
            } else {
                tDialogTitulo.setText("MC %");
            }
        });
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        etDialogReferencia.setOnKeyListener((view, i, keyEvent) -> {

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                respaldoDeET();
                double num = Double.parseDouble(etDialogReferencia.getText().toString());
                num *= apalancamiento;
                num *= referencia;


                if (esCantidadEnDialogMargen) {
                    num *= porcentajeEntero / 100;

                    if (num % 1 > 0) {
                        etCantidadMostrador.setText(String.format("%.2f", num));
                        etCantidad.setText(String.format("%.4f", num));

                    } else {
                        etCantidadMostrador.setText(String.format("%.0f", num));
                    }

                } else {
                    double porcEntero = cantidad / num;
                    porcEntero *= 100;


                    if (porcEntero % 1 > 0) {
                        etPorcentaje.setText(String.format("%.4f", porcEntero));

                    } else {
                        etPorcentaje.setText(String.format("%.0f", porcEntero));
                    }
                }

                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
                return true;

            } else
                return false;
        });
    }

    EditText etPrecioDialogPos, etPorcentajeDialogPos, etPrecisionDialogPos;
    TextView tPrecioDialogPos, tPorcentajeDialogPos, tSuperiorDialogPos, tInferiorDialogPos;
    double precioDialogPos, porcentajeDialogPos, superiorDialogPos, inferiorDialogPos;
    Button bLimpiarDialogPos, bRegresarDialogPos, bSalirDialogPos;
    String formatoDialgoPos, resPrecioDialogPos, resPorcentajeDialogPos;

    private void crearDialogPos() {
        dialog = new Dialog(getActivity(), R.style.MyDialogStyle);
        dialog.setContentView(R.layout.dialog_pos);
        dialog.show();
        etPrecioDialogPos = dialog.findViewById(R.id.et_precio_dialog_pos);
        etPrecioDialogPos.addTextChangedListener(textWatcherDialogPos);
        etPorcentajeDialogPos = dialog.findViewById(R.id.et_porcentaje_dialog_pos);
        etPorcentajeDialogPos.addTextChangedListener(textWatcherDialogPos);
        tPrecioDialogPos = dialog.findViewById(R.id.t_titulo_precio_dialog_pos);
        tPrecioDialogPos.setOnClickListener(clickListenerDialogPos);
        tPorcentajeDialogPos = dialog.findViewById(R.id.t_titulo_porcen_dialog_pos);
        tPorcentajeDialogPos.setOnClickListener(clickListenerDialogPos);
        tSuperiorDialogPos = dialog.findViewById(R.id.t_superior_dialog_pos);
        tSuperiorDialogPos.setOnClickListener(clickListenerDialogPos);
        tInferiorDialogPos = dialog.findViewById(R.id.t_inferior_dialog_pos);
        tInferiorDialogPos.setOnClickListener(clickListenerDialogPos);
        bLimpiarDialogPos = dialog.findViewById(R.id.b_limpiar_dialog_pos);
        bLimpiarDialogPos.setOnClickListener(clickListenerDialogPos);
        bRegresarDialogPos = dialog.findViewById(R.id.b_regresar_dialog_pos);
        bRegresarDialogPos.setOnClickListener(clickListenerDialogPos);
        bSalirDialogPos = dialog.findViewById(R.id.b_salir_dialog_pos);
        bSalirDialogPos.setOnClickListener(clickListenerDialogPos);
        etPrecisionDialogPos = dialog.findViewById(R.id.et_precision_dialog_pos);
        etPrecisionDialogPos.addTextChangedListener(textWatcherDialogPos);

        if (precioDialogPos != 0) {
            etPrecioDialogPos.setText(String.valueOf(precioDialogPos));
            etPorcentajeDialogPos.setText(String.valueOf(porcentajeDialogPos * 100));
        }


        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    TextWatcher textWatcherDialogPos = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!etPrecioDialogPos.getText().toString().isEmpty() &&
                    !etPrecioDialogPos.getText().toString().equals(".") &&
                    !etPorcentajeDialogPos.getText().toString().isEmpty() &&
                    !etPorcentajeDialogPos.getText().toString().equals(".")) {

                precioDialogPos = Double.valueOf(etPrecioDialogPos.getText().toString());
                porcentajeDialogPos = Double.valueOf(etPorcentajeDialogPos.getText().toString());
                porcentajeDialogPos /= 100;

                superiorDialogPos = precioDialogPos * (1 + porcentajeDialogPos);
                inferiorDialogPos = precioDialogPos * (1 - porcentajeDialogPos);

                if (etPrecisionDialogPos.getText().toString().isEmpty())
                    formatoDialgoPos = "%,.5f";
                else {
                    formatoDialgoPos = "%,." + etPrecisionDialogPos.getText().toString() + "f";
                }


                tSuperiorDialogPos.setText(String.format(formatoDialgoPos, superiorDialogPos));
                tInferiorDialogPos.setText(String.format(formatoDialgoPos, inferiorDialogPos));

            } else {
                tSuperiorDialogPos.setText("");
                tInferiorDialogPos.setText("");
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    View.OnClickListener clickListenerDialogPos = view -> {

        switch (view.getId()) {
            case R.id.t_titulo_precio_dialog_pos: {
                etPrecioDialogPos.getText().clear();
                break;
            }
            case R.id.t_titulo_porcen_dialog_pos: {
                etPorcentajeDialogPos.getText().clear();
                break;
            }
            case R.id.b_limpiar_dialog_pos: {
                resPrecioDialogPos = etPrecioDialogPos.getText().toString();
                resPorcentajeDialogPos = etPorcentajeDialogPos.getText().toString();
                etPrecioDialogPos.getText().clear();
                etPorcentajeDialogPos.getText().clear();
                precioDialogPos = 0;
                porcentajeDialogPos = 0;

                //todo checar que todavia sigue guardando lainfo 
                break;
            }

            case R.id.b_regresar_dialog_pos: {
                etPrecioDialogPos.setText(resPrecioDialogPos);
                etPorcentajeDialogPos.setText(resPrecioDialogPos);
                break;
            }

            case R.id.t_superior_dialog_pos: {
                copyToast(tSuperiorDialogPos.getText().toString()
                        .replace(",", ""));
                break;
            }

            case R.id.t_inferior_dialog_pos: {
                copyToast(tInferiorDialogPos.getText().toString()
                        .replace(",", ""));
                break;
            }

            case R.id.b_salir_dialog_pos: {
                dialog.dismiss();
                break;
            }
        }

    };


    @Override
    public void onDestroy() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tipoApalancamiento", tipoApalancamiento);
        editor.putBoolean("hayDecimales", hayDecimales);
        editor.putString("cantidad", etCantidad.getText().toString());
        editor.putString("porcentajeEntero", etPorcentaje.getText().toString());
        editor.putString("referencia", etReferencia.getText().toString());
        editor.putString("redondeoRef", String.valueOf(redondeoRef));
        editor.putInt("apalancamiento", apalancamiento);
        editor.putString("precioDialogPos", String.valueOf(precioDialogPos));
        editor.putString("porcentajeDialogPos", String.valueOf(porcentajeDialogPos));
        editor.apply();
        super.onDestroy();
    }


    View.OnLongClickListener oLClickListenerRedondeoDescendente = view -> {
        crearDialogRedondeo(false);
        return true;
    };

    View.OnLongClickListener oLClickListenerRedondeoAscendente = view -> {
        crearDialogRedondeo(true);
        return true;
    };

    View.OnLongClickListener oLClickListenerApalancamiento = view -> {
        crearDialogApalancamiento();
        return true;
    };

    View.OnClickListener clickListenerTamano = view -> {
        if (!tTamanoC.getText().toString().equals("TP"))
            crearDialogTamano();
    };

    View.OnClickListener clickListenerTamanoConversion = view -> {
        if (!tTamanoC.getText().toString().equals("TPC"))
            crearDialogTamanoConversion();
    };

    View.OnClickListener clickListenerLotes = view -> {
        if (!tTamanoC.getText().toString().equals("TP"))
            crearDialogLotes();
    };

    View.OnClickListener clickListenerLotesConversion = view -> {
        if (!tTamanoC.getText().toString().equals("TPC"))
            crearDialogLotesConversion();
    };

    View.OnClickListener clickListenerMargen = view -> {
        if (!tMargen.getText().toString().equals("Margen"))
            crearDialogMargen();
    };

    View.OnClickListener clickListenerMargenConversion = view -> {
        if (!tMargenC.getText().toString().equals("MC"))
            crearDialogTamanoMargenConversion();
    };


    View.OnClickListener onClickListener = view -> {

        switch (view.getId()) {
            case R.id.t_titulo_cant:
                respaldoDeET();
                etCantidadMostrador.getText().clear();
                yaRedondeo = false;
                break;

            case R.id.t_titulo_porcen:
                respaldoDeET();
                etPorcentaje.getText().clear();
                yaRedondeo = false;
                break;

            case R.id.t_titulo_convers:
                respaldoDeET();
                etReferencia.getText().clear();
                yaRedondeo = false;
                break;

            case R.id.b_limpiar_claro: {
                respaldoDeET();
                etCantidadMostrador.getText().clear();
                etPorcentaje.getText().clear();
                etReferencia.getText().clear();
                redondeoRef = 1000;
                tTamano.setText("TP");
                tLote.setText("Lotes");
                tTamanoC.setText("TPC");
                tLoteC.setText("LC");
                tMargen.setText("Margen");
                tMargenC.setText("MC");
                tSeguro.setText("Seguro");
                tSeguroC.setText("SC");
                yaRedondeo = false;
                resPrecioXDialogPos = precioDialogPos;
                resPorcentajeXDialogPos = porcentajeDialogPos;
                precioDialogPos = 0;
                porcentajeDialogPos = 0;
                break;
            }

            case R.id.b_regresar_claro: {

                if (seAplanoLimpiar) {

                    if (resCantidad == null)
                        break;

                    etCantidadMostrador.setText(String.format("%.2f", Double.valueOf(resCantidad)));
                    etCantidad.setText(resCantidad);
                    etPorcentaje.setText(resPorcentaje);
                    etReferencia.setText(resReferencia);
                    redondeoRef = ajusteRefRespaldo;
                    yaRedondeo = resYaRedondeo;
                    precioDialogPos = resPrecioXDialogPos;
                    porcentajeDialogPos = resPorcentajeXDialogPos;
                    seAplanoLimpiar = false;
                }
                break;

            }


            case R.id.b_apalancamiento: {

                tipoApalancamiento += 1;

                if (tipoApalancamiento > 4)
                    tipoApalancamiento = 1;

                cambioApalancamiento();
                break;


            }

            case R.id.t_titulo_tamano: {
                hayDecimales = !hayDecimales;


                if (!tTamanoC.getText().toString().equals("TPC")) {
                    if (!hayDecimales)
                        tTamanoC.setText(String.format("%,.0f", tamanoPosicionC));
                    else
                        tTamanoC.setText(String.format("%,.2f", tamanoPosicionC));
                }

                if (!tTamano.getText().toString().equals("TP")) {
                    if (!hayDecimales)
                        tTamano.setText(String.format("%,.0f", tamanoPosicion));
                    else
                        tTamano.setText(String.format("%,.2f", tamanoPosicion));

                }
                break;

            }
            case R.id.b_redondeo_descendente:
                ajusteRedondeo(false);
                break;


            case R.id.b_redondeo_ascendente:
                ajusteRedondeo(true);
                break;

            case R.id.b_pos:
                crearDialogPos();
                break;


        }

    };

    private void respaldoDeET() {
        seAplanoLimpiar = true;
        resCantidad = etCantidad.getText().toString();
        resCantidadMostrador = etCantidadMostrador.getText().toString();
        resPorcentaje = etPorcentaje.getText().toString();
        resReferencia = etReferencia.getText().toString();
        resYaRedondeo = yaRedondeo;
        ajusteRefRespaldo = redondeoRef;
    }

    private void ajusteRedondeo(boolean esAscendente) {
        double restante, restanteFinal, num,
                tamanoPosicioncAjustada, tamanoPosicionAjustada;

        resCantidadRedo = etCantidad.getText().toString();
        resPorcentajeRedo = etPorcentaje.getText().toString();
        resReferenciaRedo = etReferencia.getText().toString();

        if (!tTamanoC.getText().toString().equals("TPC")) {

            if (hayDecimales)
                tamanoPosicioncAjustada = tamanoPosicionC;
            else {

                tamanoPosicioncAjustada = Math.round(tamanoPosicionC);


                if (yaRedondeo) {
                    restante = tamanoPosicioncAjustada % redondeoRef;
                    tamanoPosicioncAjustada -= restante;

                    if (esAscendente) {
                        if (restante >= 1)
                            tamanoPosicioncAjustada += redondeoRef;
                    }
                }

            }


            restante = tamanoPosicioncAjustada % redondeoRef;
            restanteFinal = redondeoRef - restante;
            if (esAscendente)
                num = tamanoPosicioncAjustada + restanteFinal;

            else {
                if (yaRedondeo)
                    num = tamanoPosicioncAjustada - restanteFinal;
                else
                    num = tamanoPosicioncAjustada - restante;
            }

            num *= referencia;
            num *= (porcentajeEntero / 100);

            if (num % 1 > 0) {
                etCantidadMostrador.setText(String.format("%.2f", num));
                etCantidad.setText(String.format("%.4f", num));

            } else {
                etCantidadMostrador.setText(String.format("%.0f", num));
            }


        } else if (!tTamano.getText().toString().equals("TP")) {


            if (hayDecimales)
                tamanoPosicionAjustada = tamanoPosicion;
            else {
                tamanoPosicionAjustada = Math.round(tamanoPosicion);

                if (yaRedondeo) {
                    restante = tamanoPosicionAjustada % redondeoRef;
                    tamanoPosicionAjustada -= restante;

                    if (esAscendente) {
                        if (restante >= 1)
                            tamanoPosicionAjustada += redondeoRef;
                    }
                }

            }

            restante = tamanoPosicionAjustada % redondeoRef;
            restanteFinal = redondeoRef - restante;

            if (esAscendente)
                num = tamanoPosicionAjustada + restanteFinal;

            else {

                if (yaRedondeo)
                    num = tamanoPosicionAjustada - restanteFinal;
                else
                    num = tamanoPosicionAjustada - restante;
            }


            num *= (porcentajeEntero / 100);

            if (num % 1 > 0) {
                etCantidadMostrador.setText(String.format("%.2f", num));
                etCantidad.setText(String.format("%.4f", num));

            } else {
                etCantidadMostrador.setText(String.format("%.0f", num));
            }
        }

        yaRedondeo = true;
    }


    private void cambioApalancamiento() {

        switch (tipoApalancamiento) {

            case 0:
                if (getLifecycle().getCurrentState().equals(Lifecycle.State.STARTED)) {
                    if (sharedPreferences.contains("apalancamiento"))
                        apalancamiento = sharedPreferences.getInt("apalancamiento", 100);
                }
                bApalancamiento.setText(String.valueOf(apalancamiento) + "X");
                break;

            case 1:
                apalancamiento = 100;
                bApalancamiento.setText("100X");
                break;

            case 2:
                apalancamiento = 200;
                bApalancamiento.setText("200X");
                break;

            case 3:
                apalancamiento = 400;
                bApalancamiento.setText("400X");
                break;

            case 4:
                apalancamiento = 500;
                bApalancamiento.setText("500X");
                break;

        }

        if (!tTamano.getText().toString().equals("TP")) {

            margen = tamanoPosicion / apalancamiento;
            necesario = cantidad + margen;
            tMargen.setText(String.format("%,.2f", margen));
            tSeguro.setText(String.format("%,.2f", necesario));

            if (!tTamanoC.getText().toString().equals("TPC")) {
                margenC = tamanoPosicionC / apalancamiento;
                necesarioC = (cantidad / referencia) + margenC;
                tMargenC.setText(String.format("%,.2f", margenC));
                tSeguroC.setText(String.format("%,.2f", necesarioC));

            }
        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            if (!etCantidad.getText().toString().isEmpty() &&
                    !etCantidad.getText().toString().equals(".") &&
                    !etPorcentaje.getText().toString().isEmpty() &&
                    !etPorcentaje.getText().toString().equals(".")

            ) {
                cantidad = Double.parseDouble(etCantidad.getText().toString());
                porcentajeEntero = Double.parseDouble(etPorcentaje.getText().toString());
                tamanoPosicion = cantidad / (porcentajeEntero / 100);


                if (!etReferencia.getText().toString().isEmpty() &&
                        !etReferencia.getText().toString().equals(".")) {

                    referencia = Double.parseDouble(etReferencia.getText().toString());
                    tamanoPosicionC = tamanoPosicion / referencia;
                    loteC = tamanoPosicionC / 100000;
                    margenC = tamanoPosicionC / apalancamiento;
                    necesarioC = (cantidad / referencia) + margenC;
                    if (!hayDecimales)
                        tTamanoC.setText(String.format("%,.0f", tamanoPosicionC));
                    else
                        tTamanoC.setText(String.format("%,.2f", tamanoPosicionC));
                    tLoteC.setText(String.format("%.4f", loteC));
                    tMargenC.setText(String.format("%,.2f", margenC));
                    tSeguroC.setText(String.format("%,.2f", necesarioC));
                } else {

                    if (!tTamanoC.getText().toString().equals("TPC")) {
                        tTamanoC.setText("TPC");
                        tLoteC.setText("LC");
                        tMargenC.setText("MC");
                        tSeguroC.setText("SC");
                    }
                }


                lote = tamanoPosicion / 100000;
                margen = tamanoPosicion / apalancamiento;
                necesario = margen + cantidad;
                if (!hayDecimales)
                    tTamano.setText(String.format("%,.0f", tamanoPosicion));
                else
                    tTamano.setText(String.format("%,.2f", tamanoPosicion));
                tLote.setText(String.format("%.4f", lote));
                tMargen.setText(String.format("%,.2f", margen));
                tSeguro.setText(String.format("%,.2f", necesario));


            } else {

                if (!tTamano.getText().toString().equals("TP")) {
                    tTamano.setText("TP");
                    tLote.setText("Lotes");
                    tTamanoC.setText("TPC");
                    tLoteC.setText("LC");
                    tMargen.setText("Margen");
                    tMargenC.setText("MC");
                    tSeguro.setText("Seguro");
                    tSeguroC.setText("SC");
                }

            }

        }

        @Override
        public void afterTextChanged(Editable editable) {


        }
    };
    TextWatcher textWatcherMostrador = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            etCantidad.setText(charSequence);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void checadaSharedPreference() {
        sharedPreferences = getActivity().getSharedPreferences("xPos", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("tipoApalancamiento"))
            tipoApalancamiento = sharedPreferences.getInt("tipoApalancamiento", 1);
        if (sharedPreferences.contains("hayDecimales"))
            hayDecimales = sharedPreferences.getBoolean("hayDecimales", false);
        if (sharedPreferences.contains("cantidad")) {

            if (sharedPreferences.getString("cantidad", "").isEmpty()) {
                etCantidadMostrador.setText("");
            } else {
                double valor = Double.valueOf(sharedPreferences.getString("cantidad", ""));
                etCantidadMostrador.setText(String.format("%.2f", Double.valueOf(valor)));
                etCantidad.setText(String.format("%.4f", Double.valueOf(valor)));
            }
        }

        if (sharedPreferences.contains("porcentajeEntero"))
            etPorcentaje.setText(sharedPreferences.getString("porcentajeEntero", ""));
        if (sharedPreferences.contains("referencia"))
            etReferencia.setText(sharedPreferences.getString("referencia", ""));
        if (sharedPreferences.contains("redondeoRef"))
            redondeoRef = Double.parseDouble(sharedPreferences.getString("redondeoRef", "1000"));
        if (sharedPreferences.contains("apalancamiento"))
            apalancamiento = sharedPreferences.getInt("apalancamiento", 100);
        if (sharedPreferences.contains("precioDialogPos"))
            precioDialogPos = Double.valueOf(sharedPreferences.getString("precioDialogPos", "0"));
        if (sharedPreferences.contains("porcentajeDialogPos"))
            porcentajeDialogPos = Double.valueOf(sharedPreferences.getString("porcentajeDialogPos", "0"));
    }


    private void copyToast(CharSequence text) {

        ClipData clip = ClipData.newPlainText("Precio", text.toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), "Precio grabada: " + text.toString(), Toast.LENGTH_SHORT).show();
    }


}
