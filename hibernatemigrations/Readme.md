# Why hibernate.hbm2ddl.auto=update is bad choice for production

Run `GenerateDataWithUnorderedWheels` to populate cars with some wheels.

Next uncomment the line with `@OrderColumn` in `CarEntity`.
This will add order to the car's wheels.

Then run `ReadDataAfterWheelsAreOrdered`.
You will see errors in the logs:
    
    Exception in thread "main" org.hibernate.HibernateException: 
        null index column for collection: 
            io.github.navpil.dbtests.hibernatemigrations.CarEntity.wheels
    
The reason for that is that Hibernate only auto-dlls, but in this case some reasonable default should be added to a column.

The script to do this for SQL Server is the following:

    update my_wheel
    set wheel_order = wheels_with_correct_order.wheel_order
    from my_wheel join (
        select id, ROW_NUMBER() over(order by my_wheel.car_id) - min_rownum_for_car.minrownum as wheel_order
        from my_wheel
        join (
            select min(rownum) as minrownum, car_id
            from (
                select ROW_NUMBER() over(order by car_id) as rownum, id, car_id
                from my_wheel
            ) as ordered_wheels
            group by car_id
        ) as min_rownum_for_car
        on min_rownum_for_car.car_id = my_wheel.car_id
    ) as wheels_with_correct_order
    on my_wheel.id = wheels_with_correct_order.id


Alternatively with the `WITH` statement it looks better:
    
    WITH ordered_wheels as (
        select ROW_NUMBER() over(order by car_id) as rownum, id, car_id
        from my_wheel
    ), min_rownum_for_car as (
        select min(rownum) minrownum, car_id 
        from ordered_wheels
        group by car_id
    ), wheels_with_correct_order as (
        select ordered_wheels.id, ordered_wheels.rownum - min_rownum_for_car.minrownum wheel_order
        from min_rownum_for_car
        join ordered_wheels on min_rownum_for_car.car_id = ordered_wheels.car_id
    )
    update my_wheel set wheel_order = wheels_with_correct_order.wheel_order
    from my_wheel join wheels_with_correct_order on my_wheel.id = wheels_with_correct_order.id
        
Which the pseudo-code behind being something like:

    update my_wheel set wheel_order = rownum() - mrn.minrownum
    from my_wheel
    join (
        select min(rownum()) as minrownum, car_id
        from my_wheel
        order by car_id
        group by car_id
    ) as mrn
    on mrn.car_id = my_wheel.car_id
    order by my_wheel.car_id


 
