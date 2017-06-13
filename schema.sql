create table students (
				sid number(8) primary key,
			  sname varchar(20);

create table courses (
				cid number(4) primary key,
				cname varchar(20),
				credits number(1);

create table enrolled (
				sid number(8),
				cid number(4),
				primary key (sid, cid),
				foreign key (sid) references students,
				foreign key (cid) references courses);
