CKEDITOR.plugins.add( 'doc_copiesSecretPriority', {
    requires: 'widget',
    init: function( editor ) {
        editor.widgets.add( 'doc_copiesSecretPriority', {
            draggable: false,
            template:
                '<div class="o2_editorPlugin_doc_copiesSecretPriority">' +
                '<div class="o2_editorPlugin_doc_copies">000001</div>' +
                '<div class="o2_editorPlugin_doc_secret">기밀★1년</div>' +
                '<div class="o2_editorPlugin_doc_priority">특급</div>' +
                '</div>',

            // editables: {
            //     content: {
            //         selector: '.o2_editorPlugin_doc_copies; o2_editorPlugin_doc_secret; o2_editorPlugin_doc_priority',
            //         allowedContent: ''
            //     }
            // },

            allowedContent:
                'div(o2_editorPlugin_doc_copiesSecretPriority, o2_editorPlugin_doc_copies, o2_editorPlugin_doc_secret, o2_editorPlugin_doc_priority)',

            requiredContent: 'div(o2_editorPlugin_doc_copiesSecretPriority);',

            upcast: function( element ) {
                return element.name == 'div' && element.hasClass( 'o2_editorPlugin_doc_copiesSecretPriority' );
            },

            edit: function(element){
            }
        });
    }
} );