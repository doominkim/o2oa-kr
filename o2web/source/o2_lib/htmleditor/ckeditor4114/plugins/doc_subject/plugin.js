CKEDITOR.plugins.add( 'doc_subject', {
    requires: 'widget',
    init: function( editor ) {
        editor.widgets.add( 'doc_subject', {
            draggable: false,
            template:
                '<div class="o2_editorPlugin_doc_subject">문서 제목</div>',

            allowedContent:
                'div(o2_editorPlugin_doc_subject)',

            requiredContent: 'div(o2_editorPlugin_doc_subject);',

            upcast: function( element ) {
                return element.name == 'div' && element.hasClass( 'o2_editorPlugin_doc_subject' );
            },

            edit: function(element){
            }
        });
    }
} );