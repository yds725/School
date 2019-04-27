--Q1. set open to false when condition is met
create or replace function func1() returns trigger as $enrollment_trig$
  declare
    enrolled_students_number integer;

  begin
    select count(*) into enrolled_students_number
    from enrollment where course = new.course;

    update courses
      set enrolled = enrolled_students_number,
        open = case when enrolled_students_number >= (select lim from courses where num = new.course) then False else TRUE end
        where num = new.course;
    return new;
  end;

$enrollment_trig$ language plpgsql; --based on recitation 8

drop trigger if exists trig1 on enrollment;
create trigger trig1
  after insert
  on enrollment
  for each row
execute procedure func1();

-- --testing
--
-- insert into enrollment values(3, 'CS1555');
-- insert into enrollment values(4, 'CS1555');
-- insert into enrollment values(5, 'CS1555');

--Q2. prevent to add more enroll students
create or replace function func2() returns trigger as $block_trig$

  declare
    isFull boolean;

  begin
    select open into isFull from courses where num = new.course;

    if isFull = false then
      raise exception 'The course % is full!', new.course;
    end if;

    return new;
  end;

$block_trig$ language plpgsql;

drop trigger if exists trig2 on enrollment;
create trigger trig2
  before insert
  on enrollment
  for each row
execute procedure func2();

-- --testing
--
-- insert into enrollment values(3, 'CS1555');
-- insert into enrollment values(4, 'CS1555');
-- insert into enrollment values(5, 'CS1555');





