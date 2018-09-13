//-----------------------------------------------------------------
// Licensed Materials - Property of IBM
//
// WebSphere Commerce
//
// (C) Copyright IBM Corp. 2016 All Rights Reserved.
//
// US Government Users Restricted Rights - Use, duplication or
// disclosure restricted by GSA ADP Schedule Contract with
// IBM Corp.
//-----------------------------------------------------------------


module.exports = function( grunt ) {
    "use strict";

    var localesforJs = [
        "en_US",
        "fr_FR",
        "de_DE",
        "it_IT",
        "es_ES",
        "pt_BR",
        "zh_CN",
        "zh_TW",
        "ja_JP",
        "ru_RU",
        "ro_RO",
        "en",
        "fr",
        "de",
        "it",
        "es",
        "pt",
        "zh",
        "ja",
        "ru",
        "ro"
    ];

    var localesforCss = [
        "ar_EG",
        "iw_IL",
        "ja_JP",
        "ko_KR",
        "ro_RO",
        "zh_CN",
        "zh_TW"
    ];

    var localesforMobileCss = [
        "", //for general mobile css file
        "ar_EG",
        "iw_IL"
    ];

    // --------------- File Paths -----------------------
    var webContentDir_store = "./WebContent",

        ibmJsDistDir = webContentDir_store + "/WEB-INF/src/javascript",
        ibmCssDistDir = webContentDir_store + "/WEB-INF/src/css",

        storeSourceDir = webContentDir_store + "/" + grunt.option('storename'),
        storeDistDir = storeSourceDir + "/javascript",
        nlsDir = storeSourceDir + "/nls",

        distFileName = "store",
        nlsFileName = "nls";

    // Source files for store.js
    var storejsSrc = [
        // vendor.js
        ibmJsDistDir + "/vendor.js",
        // widgets.js
        ibmJsDistDir + "/widgets.js",
        
        // store JS files
        storeSourceDir + "/**/*.js",
        "!" + storeDistDir + "/" + distFileName + ".js",
        "!" + storeDistDir + "/" + distFileName + ".uncompressed.js",
        "!" + storeSourceDir + "/nls/**/*.js",
        "!" + storeSourceDir + "/mobile30/**/*.js",
        "!" + storeSourceDir + "/WorklightHybrid/**/*.js",
        "!" + storeSourceDir + "/javascript/Tealeaf/**/*.js",
        "!" + storeSourceDir + "/javascript/FBintegration.js",
        // custom JS files
        "<%= gruntProps.myWidgetsDir %>/**/*.js"
    ];

    // Source files for store.css
    var storecssSrc = [
        // store css files
        storeSourceDir + "/css/common1_1.css",
        storeSourceDir + "/css/base.css",
        storeSourceDir + "/css/styles.css",
        //vendor.css
        ibmCssDistDir + "/vendor.css",
        "<%= gruntProps.myStylesDir %>/**/*.css"
    ];

    grunt.initConfig( {
        properties: {
            gruntProps: "grunt.properties"
        },

        // Destination file paths
        storeDistUncompressedFile: storeDistDir + "/" + distFileName + ".uncompressed.js",
        storeDistFile: storeDistDir + "/" + distFileName + ".js",
        storeDistMapFile: storeDistDir + "/" + distFileName + ".map",
        storeCssUncompressedFile: storeSourceDir + "/css/" + distFileName + ".uncompressed.css",
        storeCssFile: storeSourceDir + "/css/" + distFileName + ".css",

        concat: {
            options: {
                separator: '',
            },
            storejs: {
                src: storejsSrc,
                dest: "<%= storeDistUncompressedFile %>"
            },
            storecss: {
                src: storecssSrc,
                dest: "<%= storeCssUncompressedFile %>"
            },
        },

        copy: {
            devStoreJs: {
                files: [
                    {
                        // store.uncompressed.js -> store.js
                        src: "<%= storeDistUncompressedFile %>",
                        dest: "<%= storeDistFile %>"
                    }
                ]
            },

            devStoreCss: {
                files: [
                    {
                        // store.uncompressed.css -> store.css
                        src: "<%= storeCssUncompressedFile %>",
                        dest: "<%= storeCssFile %>"
                    },
                ]
            }
        },

        clean: {
            files: [
                "<%= storeDistUncompressedFile %>",
                "<%= storeDistFile %>",
                "<%= storeDistMapFile %>",
                "<%= storeCssUncompressedFile %>",
                "<%= storeCssFile %>"
            ],
            options: {
                force: true
            }
        },

        watch: {
            storejs: {
                files: [ "<%= concat.storejs.src %>" ],
                tasks: [ "properties", "concat:storejs", "copy:devStoreJs" ]
            },
            storecss: {
                files: [ "<%= concat.storecss.src %>" ],
                tasks: [ "properties", "concat:storecss", "copy:devStoreCss" ]
            }
        },

        uglify: {
            storejs: {
                files: {
                    ["<%= storeDistFile %>"]: ["<%= storeDistUncompressedFile %>"]
                },
                options: {
                    preserveComments: false,
                    sourceMap: true,
                    sourceMapName: "<%= storeDistMapFile %>",
                    report: "min",
                    beautify: {
                        "ascii_only": true
                    },
                    compress: {
                        "hoist_funs": false,
                        loops: false,
                        unused: false
                    }
                }
            }
        },

        cssmin: {
            options: {
                shorthandCompacting: false,
                roundingPrecision: -1
            },
            storecss: {
                files: {
                    ["<%= storeCssFile %>"]: ["<%= storeCssUncompressedFile %>"]
                }
            }
        }
    } );

    // Integrate jQuery specific tasks
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-cssmin');
    grunt.loadNpmTasks('grunt-properties-reader');
    grunt.loadNpmTasks('grunt-css-import');

    // Prepare locale related JavaScipt data
    for(var i = 0; i < localesforJs.length; i++) {
        var locale = localesforJs[i];

        var nlsFile = nlsDir + "/" + locale + "/" + nlsFileName + ".js";

        // Source files for nls.js for locales
        var localeJsSrc = [
            nlsDir + "/" + locale + "/StoreText.js",
            nlsDir + "/" + locale + "/NumberFormattingData.js"
        ];

        grunt.config(['concat', locale + "_js"], {
            options: {
                separator: '',
            },
            src: [localeJsSrc],
            dest: nlsFile
        });

        grunt.config(['clean', locale + "_js"], {
            src: [nlsFile],
            options: {
                force: true
            }
        });

        grunt.config(['watch', locale + "_js"], {
            files: [ localeJsSrc ],
            tasks: [ "concat:"+locale+"_js" ]
        });
    }

    // Prepare locale related Css files
    for(var i = 0; i < localesforCss.length; i++) {
        var locale = localesforCss[i];

        var localeCssUncompressedFile = storeSourceDir + "/css/" + distFileName + locale + ".uncompressed.css",
            localeCssFile = storeSourceDir + "/css/" + distFileName + locale + ".css";

        var commonCssFile = storeSourceDir + "/css/common1_1.css";
        if (locale == "ar_EG" || locale == "iw_IL") {
            commonCssFile = storeSourceDir + "/css/common1_1" + locale + ".css";
        }

        var baseCssFile = storeSourceDir + "/css/base.css";
        if (locale == "ar_EG" || locale == "iw_IL") {
            baseCssFile = storeSourceDir + "/css/base_rtl.css";
        }

        // Source files for store.css
        var localecssSrc = [
            // store css files
            commonCssFile,
            baseCssFile,
            storeSourceDir + "/css/styles" + locale + ".css",
            //vendor.css
            ibmCssDistDir + "/vendor.css",
            "<%= gruntProps.myStylesDir %>_"+locale+"/**/*.css"
        ];

        grunt.config(['concat', locale + "_css"], {
            options: {
                separator: '',
            },
            src: localecssSrc,
            dest: localeCssUncompressedFile
        });

        grunt.config(['css_import', locale + "_css"], {
            files: {
                [localeCssUncompressedFile]: [localeCssUncompressedFile]
            }
        });

        grunt.config(['copy', "dev_" + locale + "_css"], {
            files: [
                {
                    src: localeCssUncompressedFile,
                    dest: localeCssFile
                }
            ]
        });

        grunt.config(['clean', locale + "_css"], {
            src:[
                localeCssFile,
                localeCssUncompressedFile
            ],
            options: {
                force: true
            }
        });

        grunt.config(['cssmin', locale + "_css"], {
            options: {
                shorthandCompacting: false,
                roundingPrecision: -1
            },
            files: {
                [localeCssFile]: [localeCssUncompressedFile]
            }
        });

        grunt.config(['watch', locale + "_css"], {
            files: [ localecssSrc ],
            tasks: [ "properties", "concat:"+locale+"_css",  "copy:dev_"+locale+"_css" ]
        });
    }

    // Prepare locale related mobile Css files
    for(var i = 0; i < localesforMobileCss.length; i++) {
        var locale = localesforMobileCss[i];
        var localeMobileCssFile = storeSourceDir + "/mobile30/css/mobile" + distFileName + locale + ".css";

        // Source files for mobilestore.css
        var mobileCommonCssFile = storeSourceDir + "/mobile30/css/common1_1.css";
        if (locale == "ar_EG" || locale == "iw_IL") {
            mobileCommonCssFile = storeSourceDir + "/mobile30/css/common1_1" + locale + ".css";
        }

        grunt.config(['clean', "mobile" + locale + "_css"], {
            src:[
                localeMobileCssFile
            ],
            options: {
                force: true
            }
        });

        grunt.config(['cssmin', "mobile" + locale + "_css"], {
            options: {
                shorthandCompacting: false,
                roundingPrecision: -1
            },
            files: {
                [localeMobileCssFile]: [mobileCommonCssFile]
            }
        });
    }

    // Check if storename is provided
    grunt.registerTask( "check_store", function() {
        // make sure storename param exists
        if ( grunt.option( "storename" ) == null ) {
            grunt.fail.fatal( "storename param is mandatory", [ 3 ] );
        }
        
        if (grunt.option("storename") == "skipRunning") {
            grunt.log.writeln("crsStoreName is not defined in gradle.properties - no store to compile");
            grunt.task.clearQueue();
        } else {
            var path = __dirname + "/" + webContentDir_store + "/" + grunt.option( "storename" );
            if (!grunt.file.exists(path)) {
                grunt.fail.fatal( "cannot find directory: " + path, [ 3 ] );
            }
        }
    } );

    // Short list as a high frequency watch task
    grunt.registerTask( "dev", [ "properties", "check_store", "clean", "concat", "css_import", "copy", "watch"] );

    //grunt.registerTask( "prod", [ "clean", "build:*:*", "uglify", "remove_map_comment", "dist:*" ] );
    grunt.registerTask( "prod", [ "properties", "check_store", "clean", "concat", "uglify", "css_import", "cssmin" ] );

    // default option
    grunt.registerTask( "default", [ "prod" ] );
};
