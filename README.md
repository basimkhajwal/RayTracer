# Building a functional 3D ray tracer and to what extent can it simulate realistic light behaviour.

## Introduction

In the modern world we are constantly surrounded by realistic computer generated imagery. From incredibly realistic films with awe-inspiring graphics to real-time immersive video games which provide a temporary escape into virtual reality. Out of the top 10 grossing games of 2015, 7 are based on a virtual 3D environment<sup>[[3](#3)]</sup> and would have dedicated developers working on rendering systems. The selling point of these ventures is the depth of realism which they offer<sup>[[4](#4)]</sup>  and, in recent years, this has really been a major market pull for research into more efficient rendering techniques resulting in exciting developments in the field<sup>[[5](#5)]</sup>.

Behind the scenes, these renderers have a high variety of techniques they employ in order to capture the environment as we see it. They model the interaction of light rays with a whole host of materials with increasing degrees of accuracy. In this project, I plan to investigate how these techniques are used achieve results by building my own ray tracer from scratch and evaluating how well it can render compared to existing implementations.

## Project Overview 

There are two main categories that the majority of renderers fall into. The distinction is made between techniques relying on _rasterization_ vs _ray/path tracing_. The basic methodology for ray tracing is to trace a multitude of rays for each pixel of the resulting image to compute how much light arrives from that direction. This is done by simulating the physical properties of light; including reflection, refraction and the effect of different materials on incoming light e.g. metals vs. glass. However, even for small images, the light along _millions_ of rays has to be computed resulting in slow running times. The payback for all this effort is a photo-realistic output which naturally simulates realistic phenomenon such as soft shadows, global illumination and caustics <sup>[[2](#2)]</sup>. 

In contrast, rasterization provides a faster approach by dealing directly with points and transformations. Rather than simulating the passage of light, the vertices in 3D are directly mapped into 2D screen space and, for points not on vertices, the rendering attributes are calculated by interpolating (taking a weighted average based on distance) between the vertices <sup>[[2](#2)]</sup>. The advantage of this technique is that it applies the exact transformation applied to every point which can be efficiently implemented in modern computers which are armed with powerful GPU's (Graphical Processing Units) capable of applying the same transformation to thousands points in parallel. The downside of this method is that it gives very little in terms of expressing realistic light behaviour, reflections and translucency are incredibly difficult to model and light behaviour such as shadows and ambient lighting have to be manually added using heuristics which often don't produce realistic results.

As a result, rasterization is mainly used for real-time applications such as games and interactive animations where the speed of rasterization pays off, but ray tracing is the go-to method for pre-rendered material such as films, packaging and anything relying on realistic CGI  . In this project, I plan on making a ray tracer because I am interested in investigating how and to what degree physically based models can produce accurate photo-realistic images and a real-time application isn't necessary. In addition, at the moment it is difficult to apply the same kind of optimisation for ray tracing due to hardware limitations but this is a field of active research with some very promising results <sup>[[7](#7)]</sup>. Therefore, with the advent of ever more powerful technology there is a high possibility that ray tracing could be made efficient enough for real-time purposes.

### Project Goals

For my project, I cannot hope to achieve as realistic results as are available in large team based projects but I aim to achieve atleast a good enough level of realism that can illustrate the ability of current models to be able to model realistic light processes. In order to access how well my renderer can do this, I plan on comparing the output of my renderer and that of an existing implementation on similar models. In addition, I could also compare photographs of light behavior (refraction, caustics and soft shadows) and evaluate on how well the output images from my renderer simulate those effects. 

Note: Throughout the project I will be making references and comparisons to an existing rendering system from a book called 'Physically Based Rendering Techniques'[[1](#1)] for which I will use the abbreviation PBRT (which is also the name given to the official renderer created based on the book).

### Choice of Technology

In order to build the application, I had to first decide which programming language to use to build it in. The industry standard for rendering systems is using C++ since it provides a high degree of low level support which allows you to optimise efficient code, also C++ provides very little overhead meaning its memory usage is often far lower than others. However, for this project I have decided to use a different programming language called Scala. This language is more expressive and higher level meaning it would require less program code to be written than the equivalent in C++, so would require less development time. Another advantage is that it runs on the Java Virtual Machine (JVM), as a result the program, once compiled, can be run on any system supporting Java and the JVM is a highly optimised system which will provide a fast runtime (although not as fast as C++). 

A useful tool in helping my decision was an experimental ray tracer <sup>[[6](#6)]</sup> which was run as a benchmark to compare the performances of languages. It showed that Scala is roughly 3 times slower than the same algorithm written in C++, but it used less than half the number of lines of code. For a large project with many developers C++ is a good choice, but in my case Scala's conciseness outweighs the relatively small performance loss. In addition, Scala is easier to maintain and debug therefore even more time would be saved in development when reasoning about the algorithms.

## Project Development

### Overview of the Ray Tracing algorithm

The overall rendering algorithm I will use throughout the rest of my project, as mentioned earlier, will be the ray tracing algorithm. Within my code, I have gone deeper into breaking down each stage of the rendering process into separate into sections which can then be developed separately and linked together to form the final product. A major benefit of separating the sections is that it will make the code much simpler to maintain and test for bugs. For example, if I notice that the image is actually upside down then I could quickly trace that there is an error in the implementation of my file saving code. However, each time I separate out the code it will create more latency and waste time communicating between each section of the program so a balance needs to be made to decide how many sections I will create.

My final decision for sections was mainly influenced by reading the code of the implementations of the code of the PBRT render<sup>[[9](#9)]</sup> and from the Minilight<sup>[[6](#6)]</sup> renderer. Below is a simplified illustration of how the sections link together and a brief explanation of their roles.

<p align="center">
<strong>
  Parser -> Sampler -> Camera -> Integrator
</strong>
</p>

First, when the program is run an input file is required which contains a file describing the image to be rendered. It is the role of the parser to read in this file and from it create a sequence of rendering commands. For example, it could specify to render a sphere at location (0, 0, 0) with a radius of 5 and a material of red plastic and to point the camera so that the sphere is in the centre of the screen. The file will also specify what to output, e.g. the dimensions of the image and where to save the file once it has been rendered.

Next, it is the role of the sampler to pick which points on the screen to fire rays through. The most basic implementation would select a single light ray through the centre of each pixel in the image. However, this doesn't always produce the best results for more complex images where multiple rays will need to be fired for each pixel and in different locations. Once a location has been chosen, it is the role of the camera to actually generate the ray by taking into account parameters such as depth of field which help to simulate realistic camera output.

Last but not least, the integrator is where the main work of this project lies in. This section is responsible for taking a light ray (as generated by the camera) and working out how much light enters the camera through that light ray. It has to take into account all the objects and lights in the scene and how different materials interact with the light. The inner mechanism of the integrator will be described in depth in a later section.

### Parsing Existing Scene Formats

In order to be able to properly compare the output of my ray tracer with existing realistic implementations I would need to be able to render the exact same images which I could then use to evaluate the effectiveness of my ray tracer. For this purpose, I chose to write a scene parser that would read in a text file in the format specified by PBRT <sup>[[8](#8)]</sup> and then use that to load the scene which would then be rendered using the ray tracer. The main difficulty in this was that I needed to follow the exact specification of the scene format in order to be able to read everything without coming across errors. In other parts of the project I was free to make modifications that would simplify the code and make it more efficient to do in Scala (since most existing implementations are in C++), however for the parsing I would have to implement all the details but I would still be free as to _how_ I would parse the details.

The parsing process itself is composed of a series of well-defined steps each which process the input file further in order to be used for the next stage. This process is very general and is commonly used in most parsing systems however, since the PBRT file format is quite simple the parsing that I will need to do will be relatively simple compared more complex parsing systems that are designed to operate on much more intricate language definitions. The diagram below illustrates the simplified pipeline that I have used in my own parsing system from the input file to the final image output.

<p align="center">
  <img src="progress/process.png" alt="Parsing Process" />
</p>

The first stage involves reading the stream of characters from the input file given to the program, then a process called _tokenising_ is performed. This heavily simplifies the input by removing comments, whitespace and sectionining groups of characters into entities known as _tokens_. Luckily for me, tokenising is a very common procedure and there was a builtin class in the Scala libraries called StreamTokeniser which handled most of heavy duty work and all I had to implement was a thin wrapper over it so it would function according to my needs. Now, the input file has been transformed from a stream of characters into a stream of tokens.

Consequently, the second stage reads in these tokens and builds a sequence of rendering commands out of them. This was one of the more complex tasks since it was specific to the PBRT file format and I needed to match the specification exactly doing this process. Each rendering command would require multiple tokens and I built a whole set of parsing functions in order to be handle each type. In order to do this, I consulted the specification<sup>[[8](#8)]</sup> as mentioned but also the existing source code<sup>[[9](#9)]</sup> which allowed me to double check to make sure that the logic I used was correct in my own code and also as a guideline to structure my own code. For example, when reading the input file it could end up referencing another file to read input out of, as a result the second stage would have to call the first stage again to ask for more tokens from a different file. This process could have been quite complex to implement but, using the existing implementation, I saw the original creators had the idea to use a data structure called a stack which helped me to simplify my code. 

Eventually, each rendering command would then be passed onto the rendering pipeline, the details of which have been the topic of discussion of the rest of the report. From there the final image would then be generated using all the various rendering techniques and options set by the image file. In the end, I am quite happy with the results of my parser and I managed to read the vast majority of the file format (some parts were omitted for simplicity) using just under 1100 lines of code - compared to the C++ Parser used within the actual PBRT implementation which uses about 4500 lines of code. This further highlights how using Scala has been a good tool to increase productivity and reduce how much code will be needed. 

### Image Sampling Strategies

The process of sampling determines from which parts of screen the 

- Describe why an effective sampling strategy is required
- Compare and constrast the difference between using a random sampler and a more effective sampler (e.g. Stratified with jittering)

### Camera Simulation

An integral part of the ray tracing process is to be able to actually generate the rays that will eventually be traced against the scene for incoming light sources. In my ray tracer, the generation of these light rays is abstracted by the 'Camera' interface.  For each ray, two main things are required: the start position and it's direction. 

The first type of camera is called an Orthographic camera, this camera generates rays by starting at the location on the screen and setting the direction to be going directly out of the image, perpendicular to the screen. As a result, using an orthographic camera will result in an image which looks 'flat', i.e. parallel lines in the scene remain paralell lines in the image. Although this can be a desirable property for testing, a realistic camera implementation does not have this quality and instead maps objects depending on how far away they are from the camera. In an orthographic camera, the object would look the same size no matter how far away it was hence it doesn't often result in an authentic image output.

A better camera simulation is by using a perspective camera, this camera takes into account the distance of an object so that objects which are further away appear proportionally smaller. As a result, an extra parameter is required when creating a perspective camera: the FOV (field of view). This is a single number which specifies the range of vision of the camera ranging from 0° to 180°, to simulate typical human vision the FOV is generally set to around 60° to 70°<sup>[[6](#6)]</sup>.

Both these camera implementations can be implemented simply as a projection by applying the same transformation for each incoming sample location. The orthographic camera will apply an orthographic projection (as described above) and the perspective camera will apply a perspective projection. This projection must be efficient as it will be applied hundres of thousands of time for each image rendered, hence I decided to use a matrix multiplication to combine all the steps into one computation. The matrix I have used for each projection is a commonly used matrix, for which one of the first descriptions was by Carlbrom and Paciorek<sup>[[6](#6)]</sup>. In my raytracer, I used their definition for orthographic and perspective projections in order to generate rays. Below is a comparsion of both camera implementations:

- TODO: Create an image showing a comparison of both orthographic and perspective cameras

### Optimisation Strategies

- First describe why optimisation is neccessary
- Then describe low level specific optimisations i.e. a 30% improvement
- Then describe the need for an acceleration data structure
- Describe how I used profiling and techniques learnt from online sources to debug the sources of slowness in my code


## References

##### 1 
###### Pharr, M., Humphreys, G. and Jakob, W. (2010) Physically based rendering: From theory to implementation - 2nd edition. 2nd edn. Amsterdam: Elsevier/Morgan Kaufmann Publishers.

##### 2 
###### Scratchapixel (no date) Available at: https://www.scratchapixel.com/ (Accessed: 8 January 2017).

##### 3
###### Romanyuk, S. (2016) Mobile Moba - a risky lane. Available at: http://www.wetapgame.com/2016/04/25/mobile-moba-risky-lane/ (Accessed: 8 January 2017).

##### 4
###### Low, G.S. (2001) ‘Understanding Realism in Computer Games through Phenomenology’, .

##### 5
###### Price, A. (2015) 24 Photorealistic blender renders. Available at: http://www.blenderguru.com/articles/24-photorealistic-blender-renders/ (Accessed: 8 January 2017).

##### 6
###### Ainsworth, H. (2013) < H X A 7 2 4 1 >: Minilight. Available at: http://www.hxa.name/minilight/ (Accessed: 10 January 2017).

##### 7
###### Altman, R. (2016) Raytracing today and in the future - Randi Altman’s postPerspective. Available at: http://postperspective.com/ray-tracing-today-and-in-the-future/ (Accessed: 25 January 2017).

##### 8
###### Pharr, M. (2014). pbrt-v2 Input File Format. [online] Pbrt.org. Available at: http://www.pbrt.org/fileformat.html [Accessed 22 Mar. 2017].

##### 9
###### Pharr, M. (2017). mmp/pbrt-v2. [online] GitHub. Available at: https://github.com/mmp/pbrt-v2 [Accessed 22 Mar. 2017].

##### 10
###### Carlbom, I. and Paciorek, J. (1978). Planar Geometric Projections and Viewing Transformations. ACM Computing Surveys, 10(4), pp.465-502.

