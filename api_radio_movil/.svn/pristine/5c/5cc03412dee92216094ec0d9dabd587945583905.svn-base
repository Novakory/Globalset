use invoice;

SELECT * FROM years y
	JOIN periods p ON p.id_year = y.id
    JOIN spents s ON s.id_period = p.id
    WHERE y.title = '2024' AND p.title = 'Agosto';
    
/*GET YEARS*/
SELECT * FROM years;

/*GET PERIODS BY YEAR*/
SELECT p.id,p.title,p.id_year,y.title AS year FROM periods p
	JOIN years y ON y.id = p.id_year
    WHERE y.title = '2024';
    
/*GET spents BY period AND year*/
SELECT s.* FROM years y
	JOIN periods p ON p.id_year = y.id
    JOIN spents s ON s.id_period = p.id
    WHERE y.title = '2024' AND p.title = 'Agosto';
    
    
/*GET spents BY year*/
SELECT s.* FROM years y
	JOIN periods p ON p.id_year = y.id
    JOIN spents s ON s.id_period = p.id
    WHERE y.title = '2024' LIMIT 10;
    
DELIMITER //
CREATE PROCEDURE sp_grupos_por_periodo(IN year_title VARCHAR(50))
BEGIN
    DECLARE fin INT DEFAULT 0;
    DECLARE period_id INT;
    DECLARE lista_periodos CURSOR FOR 
        SELECT p.id FROM periods p
        JOIN years y ON p.id_year = y.id
        WHERE y.title = year_title;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET fin = 1;
    
    OPEN lista_periodos;
    
    grupo_loop: LOOP
        FETCH lista_periodos INTO period_id;
        IF fin THEN
            LEAVE grupo_loop;
        END IF;

        -- Ejecuta la consulta para este período específico
        SELECT s.*
        FROM spents s
        WHERE s.id_period = period_id;

    END LOOP;

    CLOSE lista_periodos;
END //
DELIMITER ;

/*GET spents BY year*/
CALL sp_grupos_por_periodo('2024');


insert into hist_subscriptions(title,price,payment_day,id_subscription) values('Starlink',1100,4,1);
select * from hist_subscriptions;
/*insertPaymentSubscriptions*/
select * from hist_subscriptions;
select * from periods;
INSERT INTO payment_subscriptions(id_hist_subscription,id_period) VALUES(1,1),(1,2),(1,3),(1,4);

/*getPaymentSubscriptionsByIdPeriod*/
SELECT h.* FROM payment_subscriptions p
	JOIN hist_subscriptions h ON h.id = p.id_hist_subscription
	WHERE id_period = 1;


/*procedimiento que devolvera las subscripciones como arrays agrupados por id_subsciption*/
INSERT INTO subscriptions(id) values(null);
select * from subscriptions;
insert into hist_subscriptions(title,price,payment_day,id_subscription) values('Modem Telcel',200,23,5);
update subscriptions set state='Inactive' where id = 5

SELECT id_subscription from hist_subscriptions h
JOIN subscriptions s ON s.id = h.id_subscription
WHERE s.state = 'Active'
GROUP BY id_subscription


DELIMITER //
CREATE PROCEDURE sp_get_group_subscriptions()
BEGIN
    DECLARE fin INT DEFAULT 0;
    DECLARE idSubscription INT;
    DECLARE list_subscriptions CURSOR FOR     
		SELECT id_subscription from hist_subscriptions h
		JOIN subscriptions s ON s.id = h.id_subscription
		WHERE s.state = 'Active'
		GROUP BY id_subscription;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET fin = 1;
    
    OPEN list_subscriptions;
    
    grupo_loop: LOOP
        FETCH list_subscriptions INTO idSubscription;
        IF fin THEN
            LEAVE grupo_loop;
        END IF;

        -- Ejecuta la consulta para este período específico
        SELECT h.*
        FROM hist_subscriptions h
        WHERE h.id_subscription = idSubscription;
        
    END LOOP;

    CLOSE list_subscriptions;
END //
DELIMITER ;

call sp_get_group_subscriptions();

SELECT * FROM payment_subscriptions p
/*JOIN hist_subscriptions h ON h.id = p.id_hist_subscription*/
RIGHT JOIN hist_subscriptions h ON h.id = p.id_hist_subscription
JOIN subscriptions s ON s.id = h.id_subscription

WHERE s.state = 'Active'
GROUP BY id_subscription
WHERE p.id_period = 1;