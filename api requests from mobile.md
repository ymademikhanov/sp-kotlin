# Requests from mobile application to backend

We can divide requests from mobile device into three types:
1.   student requests.
2.   application requests.
3.   instructor requests. 

## Student Requests
These type of request includes:
1.   sign up. Email, password, firstname and lastname. Parsing names from email is not always successful. Example: b.sarsenov@nu.edu.kz
2.   sign in.
3.   getting semester schedules. This should be specific to student.
4.   getting attendance history for course section by specifying course ID. We will show course section title.
5.   resetting the password (not urgent).

Backend should identify a student from request via tokens or sessions.


### Request formats:
1.  `POST /auth/signup HTTP/1.1 Content-Type: application/json {"email" : "student@nu.edu.kz", "password" : "qwerty321", "firstname" : "Firstname", "lastname" : "Lastname" }`
    - Expected response is JSON object: `{"successful" : false, "message" : "already registered"}`
2.  `POST /auth/signin HTTP/1.1 Content-Type: application/json {"email" : "student@nu.edu.kz", "password" : "qwerty321"}`
    - Expected response is JSON object: `{"successful" : true, "message" : "ok"}`
3.  `GET /schedule/all HTTP/1.1 Content-Type: application/json`
    - Expected response is JSON array: `{ [ {"title": "Fall 2018", "course_section_list": [ {"course_section_id" : "CSCI151Fall2018Section"} ] ... ] }`.
4.  `GET /attendance/history HTTP/1.1  Content-Type: application/json {"course_section_id" : "CSCI151Fall2018Section", "type" : "overview/detailed"}`
    - Expected response for type overview is JSON object: `{"attendended" : "34", "missed" : "1", "excused" : "0"}`.
    - Expected response for type detailed is JSON array: `{ [ {"date" : "22.09.2018", "result" : "attended/missed/excused"}, ... ] }`.
5.  later...

## Application requests
These includes:
1.   retrieving the semester schedule that are available in database.
2.   reporting about successful attendance partial check (detecting beacon) at certain time during a lecture.
3.   retrieving an attendance check scheduling for a student, since we want make it random and scheduled so that 
too many BT devices won't overwhelm beacon.
4.   sending a 2nd step checks (taking picture of a student).

### Request formats:
1.  `GET /schedule/current`
    - Expected response is JSON: `{"title": "Fall 2018", "course_section_list": [ {"course_section_id" : "CSCI151Fall2018Section", ""} ]` ---> to be edited. We should discuss the structure of courses.
2.  `POST /attendance/report HTTP/1.1 Content-Type: application/json { "course_section" : "CSCI151Fall2018Section", "reporter_email" : "student@nu.edu.kz", "check_type" : 1, "check_number" : 2 }`
    - Expected response is JSON: `{"recorded" : true}`.
3.  `GET /attendance/scheduling HTTP/1.1 Content-Type: application/json { "course_section_id" : "CSCI151Fall2018Section"}`. Backend should identify a student from request. Tokens or sessions.
    -  Expected response is JSON: `{"checks_number" : 3, "checks_times" : [ { "datetime" : "dd.mm.yyyy hh.mm.ss"} , ...] }`. It contains the number of checks to be done and the time at which the check occurs.
4.  `POST /attendance/report HTTP/1.1 Content-Type: application/json { "course_section" : "CSCI151Fall2018Section", "reporter_email" : "student@nu.edu.kz", "check_type" : 2, "check_number" : 1, "data" : "binary image"}`
    - Expected response is JSON: `{"recorded" : true}`.

## Instructor requests:

These will be similar to student requests and we can adapt responses from backend like instead of returning a single object about attendance we can return array of attendances for enrolled. Discussion is needed.

## P.S. this is not final version. Any suggestions are welcome. 
