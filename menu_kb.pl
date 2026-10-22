% =====================================================================
% BASE DE CONOCIMIENTO PROLOG: GENERADOR DE MENÚS Y RESTRICCIONES
% =====================================================================

% --- HECHOS: Alimentos y Preparaciones ---
% food(ID, Nombre, PorcionDefinida, KcalPorPorcion, TiempoComida)

% Desayuno (20% a 30% de la TMB)
food(f_huevo_mex, 'Huevo a la Mexicana (2 pzas con jitomate y cebolla)', '1 plato', 180, desayuno).
food(f_avena_lec, 'Avena cocida con leche descremada y manzana', '1 taza', 220, desayuno).
food(f_pan_aguacate, 'Pan tostado integral con aguacate y panela', '2 piezas', 250, desayuno).
food(f_yogur_frutos, 'Yogur griego natural con frutos rojos', '1 taza', 200, desayuno).
food(f_omelette_espinaca, 'Omelette de 2 huevos con espinacas y frijoles', '1 plato', 320, desayuno).
food(f_hotcakes_avena, 'Hot cakes de avena y platano con miel ligera', '3 piezas', 350, desayuno).
food(f_chilaquiles_pollo, 'Chilaquiles verdes con pechuga deshebrada y queso', '1 plato', 380, desayuno).
food(f_sandwich_huevo, 'Sandwich integral con huevo, jamon de pavo y aguacate', '2 piezas', 420, desayuno).
food(f_burrito_integral, 'Burrito de frijol con queso panela y pico de gallo', '1 pieza', 450, desayuno).

% Almuerzo / Colacion Matutina (10% a 15% de la TMB)
food(f_manzana, 'Manzana fresca', '1 pieza mediana', 95, almuerzo).
food(f_almendras, 'Almendras tostadas', '30 gramos', 170, almuerzo).
food(f_barra_amaranto, 'Barra de amaranto con miel', '1 pieza', 120, almuerzo).
food(f_gelatina_light, 'Gelatina ligera', '1 taza', 40, almuerzo).
food(f_yogur_nueces, 'Yogur natural con nueces y arandanos', '1 taza', 190, almuerzo).
food(f_tostadas_aguacate, 'Tostadas horneadas con aguacate y queso fresco', '2 piezas', 220, almuerzo).
food(f_batido_proteina, 'Batido de proteina con leche de almendras y platano', '1 vaso (300ml)', 240, almuerzo).

% Comida (30% a 40% de la TMB)
food(f_pollo_arroz, 'Pechuga asada con arroz y ensalada verde', '1 porcion completa', 450, comida).
food(f_filete_pure, 'Filete de res a la plancha con pure de papa', '1 porcion completa', 520, comida).
food(f_pasta_atun, 'Pasta integral con atun y verduras al vapor', '1 plato hondo', 480, comida).
food(f_lentejas_nopal, 'Sopa de lentejas con nopal y agua de limon', '1 plato', 410, comida).
food(f_pollo_mole, 'Pechuga de pollo con mole ligero y arroz rojo', '1 porcion', 510, comida).
food(f_carne_frijoles, 'Carne asada magra con frijoles de la olla y nopales', '1 plato grande', 550, comida).
food(f_salmon_quinoa, 'Filete de salmon a la plancha con quinoa y esparragos', '1 porcion completa', 580, comida).
food(f_pechuga_pasta, 'Pechuga rellena de espinacas con pasta ligera', '1 porcion completa', 620, comida).

% Merienda / Colacion Vespertina (10% a 15% de la TMB)
food(f_nueces_mixtas, 'Nueces mixtas', '25 gramos', 160, merienda).
food(f_yogur_bebible, 'Yogur light para beber', '1 botella (220ml)', 110, merienda).
food(f_galletas_integ, 'Galletas integrales con te sin azucar', '4 piezas', 130, merienda).
food(f_papaya_limon, 'Papaya picada con limon', '1 taza', 70, merienda).
food(f_smoothie_verde, 'Smoothie verde de espinaca, pina y chia', '1 vaso (350ml)', 150, merienda).
food(f_rollitos_pavo, 'Rollitos de pechuga de pavo rellenos de queso panela', '3 piezas', 180, merienda).
food(f_manzana_cacahuate, 'Rebanadas de manzana con crema de cacahuate', '1 plato mediano', 195, merienda).
food(f_pan_crema_almendra, 'Pan tostado integral con crema de almendras', '1 rebanada', 215, merienda).

% Cena (20% a 25% de la TMB)
food(f_pollo_nopales, 'Pechuga deshebrada con nopales asados', '1 plato', 220, cena).
food(f_quesadillas_panela, 'Quesadillas de queso panela en tortilla de maiz', '2 piezas', 260, cena).
food(f_ensalada_atun_cen, 'Ensalada de atun con jitomate y aceite de oliva', '1 plato', 240, cena).
food(f_crema_calabaza, 'Crema ligera de calabaza con crotones', '1 taza', 200, cena).
food(f_filete_pescado, 'Filete de pescado blanco con verduras al vapor', '1 porcion', 290, cena).
food(f_tostadas_tinga, 'Tostadas horneadas de tinga de pollo con lechuga', '2 piezas', 310, cena).
food(f_molletes_ligeros, 'Molletes integrales con frijoles y queso panela', '2 mitades', 330, cena).
food(f_omelette_champinones, 'Omelette de claras con champinones y queso Oaxaca', '1 plato', 340, cena).
food(f_tacos_bistec, 'Tacos de bistec asado con tortilla de maiz y nopal', '3 piezas', 355, cena).
food(f_sandwich_atun, 'Sandwich integral con ensalada de atun y aguacate', '1 pieza doble', 380, cena).


% --- REGLAS DE GENERACIÓN Y VALIDACIÓN ---

generar_menu(DesayunoID, AlmuerzoID, ComidaID, MeriendaID, CenaID, TotalKcal) :-
    food(DesayunoID, _, _, KDes, desayuno),
    food(AlmuerzoID, _, _, KAlm, almuerzo),
    food(ComidaID, _, _, KCom, comida),
    food(MeriendaID, _, _, KMer, merienda),
    food(CenaID, _, _, KCen, cena),
    TotalKcal is KDes + KAlm + KCom + KMer + KCen.

validar_restricciones(TMB, DesayunoID, AlmuerzoID, ComidaID, MeriendaID, CenaID, TotalKcal) :-
    generar_menu(DesayunoID, AlmuerzoID, ComidaID, MeriendaID, CenaID, TotalKcal),
    
    food(DesayunoID, _, _, KDes, desayuno),
    food(AlmuerzoID, _, _, KAlm, almuerzo),
    food(ComidaID, _, _, KCom, comida),
    food(MeriendaID, _, _, KMer, merienda),
    food(CenaID, _, _, KCen, cena),
    
    % Restricción Desayuno (20% - 30%)
    MinDes is TMB * 0.20, MaxDes is TMB * 0.30,
    KDes >= MinDes, KDes =< MaxDes,
    
    % Restricción Almuerzo (10% - 15%)
    MinAlm is TMB * 0.10, MaxAlm is TMB * 0.15,
    KAlm >= MinAlm, KAlm =< MaxAlm,
    
    % Restricción Comida (30% - 40%)
    MinCom is TMB * 0.30, MaxCom is TMB * 0.40,
    KCom >= MinCom, KCom =< MaxCom,
    
    % Restricción Merienda (10% - 15%)
    MinMer is TMB * 0.10, MaxMer is TMB * 0.15,
    KMer >= MinMer, KMer =< MaxMer,
    
    % Restricción Cena (20% - 25%)
    MinCen is TMB * 0.20, MaxCen is TMB * 0.25,
    KCen >= MinCen, KCen =< MaxCen,
    
    % Restricción Total del Menú (90% - 110% de la TMB)
    MinTot is TMB * 0.90, MaxTot is TMB * 1.10,
    TotalKcal >= MinTot, TotalKcal =< MaxTot.

obtener_detalle(ID, Nombre, Porcion, Kcal, Tiempo) :-
    food(ID, Nombre, Porcion, Kcal, Tiempo).