package com.enernoc.open.oadr2.vtn

import quicktime.std.movies.media.FlashDescription;


class ProgramController {
    def messageSource
    
    static defaultAction = 'programs'
    
    /**
     * Base method called to access the default page for the Programs controller
     *
     * @return a redirect to the programs() call as to render the default page
     */
    def index() {
        return redirect(action: "programs")
    }

    /**
     * Default method to render the page for the Program table
     *
     * @return the default render page for Program display and deletion
     */

    def programs() {
        /**
         * Comparator to sort the Program table by name
         *
         * @author Jeff LaJoie
         *
         class ProgramFormComparator implements Comparator<Program>{
         public int compare(Program programOne, Program programTwo){
         return programOne.getProgramName().compareTo(programTwo.getProgramName())
         }
         }
         List<Program> programs = JPA.em().createQuery("FROM Program").getResultList()
         Collections.sort(programs, new ProgramFormComparator())
         return ok(views.html.programs.render(programs))*/
        def results = Program.listOrderByProgramName(order:"desc")
        [programList: results]
    }

    /**
     * Called upon submission of the Create Program button
     *
     * @return a rendering of the Program creation form with all fields blank
     public static Result blankProgram(){
     return ok(views.html.newProgram.render(form(Program.class)))
     }*/

    def blankProgram() {
        def model = [:]
        if ( ! flash.chainModel?.program ) 
            model.program = new Program(programName:"New program",programURI:"http://openadr.org")
        model
    }

    /**
     * Persists the form from the newProgram page to the Program table
     *
     * @return a redirect to the default display page with the Program added
     @Transactional
     public static Result newProgram(){
     Form<Program> filledForm = form(Program.class).bindFromRequest()
     if(filledForm.hasErrors()){
     return badRequest()
     }
     else{
     Program newProgram = filledForm.get()
     JPA.em().persist(newProgram)
     flash("success", "Program as been created")
     }
     return redirect(routes.Programs.programs())
     }*/

    def newProgram() {
        def program = new Program(params)
        if ( program.validate() ) {
            program.save()
            flash.message = "Success, your Program has been created"
        } 
        else {
            flash.message="Please fix the errors below: "
            def errors = program.errors.allErrors.collect {
                messageSource.getMessage it, null
            }
            return chain(action:"blankProgram", model:[errors: errors, program: program])
        }
        redirect action: "programs"
    }
    /**
     *
     * @param id - the database ID of the Program to be deleted
     * @return a redirect to the default display page without the deleted Program
     */
    /*@Transactional
     public static Result deleteProgram(Long id){
     Program program = JPA.em().find(Program.class, id)
     flash("success", "Program has been deleted")
     JPA.em().remove(program)
     return redirect(routes.Programs.programs())
     }*/

    def deleteProgram() {
        def program = Program.get(params.id)
        program.delete()
        redirect(action:"programs")
        //render(params.id)
    }
    
    /**
     * Edits the program with the given id
     * 
     */
    def editProgram() {
        def currentProgram = Program.get( params.id )
        [currentProgram: currentProgram]
    }
    
    /**
     * Updates the program from editProgram() with new user input
     * If fails return to editProgram() else programs()
     * 
     */
    def updateProgram() {
        def oldProgram = Program.get( params.id )
        def newProgram = new Program(params)
        newProgram.id = oldProgram.id
        if (newProgram.validate()) {
            oldProgram.ven.each { v ->
                oldProgram.removeFromVen(v)
                newProgram.addToVen(v)
                v.programID = newProgram.programName
            }
            oldProgram.event.each { e ->
                oldProgram.removeFromEvent(e)
                newProgram.addToEvent(e)
                e.programName = newProgram.programName
            }
            newProgram.save()
            oldProgram.delete()
            flash.message = "Success, your Program has been updated"
        } else {
            flash.message="Please fix the errors below: "
            def errors = newProgram.errors.allErrors.collect {
                messageSource.getMessage(it, null)
            }
            return chain(action:"blankProgram", model:[errors: errors])
        }
        redirect(action:"programs")    
        }

}