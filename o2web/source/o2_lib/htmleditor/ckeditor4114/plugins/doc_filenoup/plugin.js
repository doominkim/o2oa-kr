CKEDITOR.plugins.add( 'doc_filenoup', {
    requires: 'widget',
    init: function( editor ) {
        editor.widgets.add( 'doc_filenoup', {
            draggable: false,
            template:
                '<table class="o2_editorPlugin_doc_filenoup" width="100%" cellpadding="0" cellspacing="0" border="0">' +
                '   <tr><td class="o2_editorPlugin_doc_filenoup_td">' +
                '       <span>　</span><span class="o2_editorPlugin_doc_filenoup_fileno">○○발〔2019〕20호</span>' +
                '   </td><td class="o2_editorPlugin_doc_filenoup_td_signer">' +
                '       <table class="o2_editorPlugin_doc_filenoup_signer_table" cellpadding="0" cellspacing="0" border="0">' +
                '           <tr><td class="o2_editorPlugin_doc_filenoup_signer_td">' +
                '               <span class="o2_editorPlugin_doc_filenoup_signer">서명자:</span>' +
                '           </td><td class="o2_editorPlugin_doc_filenoup_signerContent_td">' +
                '               <span class="o2_editorPlugin_doc_filenoup_signerContent">홍길동　김철수　이영희　박민수</span><span>　</span>' +
                '           </td></tr>' +
                '       </table>' +
                '   </td></tr>' +
                '</table>',

            // editables: {
            //     content: {
            //         selector: '.o2_editorPlugin_doc_copies; o2_editorPlugin_doc_secret; o2_editorPlugin_doc_priority',
            //         allowedContent: ''
            //     }
            // },

            allowedContent:
                'div(o2_editorPlugin_doc_filenoup)',

            requiredContent: 'div(o2_editorPlugin_doc_filenoup);',

            upcast: function( element ) {
                return element.name == 'div' && element.hasClass( 'o2_editorPlugin_doc_filenoup' );
            },

            edit: function(element){
            }
        });
    }
});