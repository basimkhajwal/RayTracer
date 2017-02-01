# EPQ Diary

##### 14/12/16
I have begun working on a prototype for my Ray Tracer, in order to test if the project is feasible whilst it is being approved by my supervisor. It will render basic spheres in a hard coded scene.

##### 18/12/16
So far, development on the prototype has been going well I've been able to accurately render spheres with reflections and corrrect calculations of the geometry and intersections, this gives me confidence that my project will be feasible and a better insight into which tasks will require the most effort to complete.

<img src="progress/ganttchart.png" alt="Gantt Chart" height="450" align="right"/>
##### 02/01/17
Using information gained from the prototype, I have begun to think about dividing the project into separate manageable tasks which will all contribute to the final product. In order to manage my workflow, I used the website GanttPro to generate a Gantt chart which I can use to work against the final report deadline and measure how well I am keeping up with progress. For smaller tasks, I intend to maintain a TODO-list on the website Trello which I will use to structure the tasks from the Gantt chart.

##### 06/01/17
Today I completed my project review, my supervisor approved of my project proposal and I plan on continuing the project as planned.

##### 18/01/17
The rendering code has been going well with each part working as I would have expected so far, this week I have also put a start on writing Unit Tests which I run every now and then to ensure that new additions to the code don't break other parts which should be functioning properly.

##### 25/01/17
I had a lot of difficulty today implementing the next stage of the ray tracer, using projective cameras to simulate how light passes through a camera to create rays rather than manually creating them as was done before. It took me over 2 hours to find the relevant bugs which turned out be inside the Transform class when chaining together sequential transformations (e.g. doing a rotation then a translation), I initially thought the bugs were in my Camera implementations which was why it took such a long time.

Eventually, I manually used a debugger to step through my code line by line until I realised where the error was happening. For me, this has highlighted how important it is to create more unit tests at each step, I plan on adding more so that I don't waste time finding simple bugs in the future. In the end, however, I managed to get my Camera implementations working correctly which was no small relief and they have produced some nice results.

##### 01/02/17
This week I haven't made much progress on the actual code of the ray tracer itself, the reason being there is a lot of theory on illuminance and reflectance which I will have to learn before I can implement any further additions to the code. I am planning on researching how to implement scattering distribution functions and apply them to my shape and intersection classes in order to obtain a more accurate simulation of the light itself.
