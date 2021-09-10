/**
 * Creating a sidebar enables you to:
 - create an ordered group of docs
 - render a sidebar for each doc of that group
 - provide next/previous navigation

 The sidebars can be generated from the filesystem, or explicitly defined here.

 Create as many sidebars as you want.
 */

module.exports = {
	// By default, Docusaurus generates a sidebar from the docs folder structure
	// sidebar: [{ dirName: '.', type: 'autogenerated' }]

	// But you can create a sidebar manually
	sidebar: [
		'intro',
		{
			type: 'category',
			label: 'Getting Started',
			items: [
				'getting-started/installation',
				'getting-started/hello-world'
			]
		},
		{
			type: 'category',
			label: 'How It Works',
			items: [
				'how-it-works/high-level',
				'how-it-works/mid-level',
				'how-it-works/low-level'
			]
		},

		{
			type: 'category',
			label: 'Guides',
			items: [
				{
					type: 'category',
					label: 'Content Authoring',
					items: [
						{
							type: 'category',
							label: 'Workflows',
							items: [
								'guides/workflow-authoring/editor',
								'guides/workflow-authoring/normalize',
								'guides/workflow-authoring/general-context',
								'guides/workflow-authoring/resource-context',
								'guides/workflow-authoring/policy-context'
							]
						},
                        {
                            type: 'category',
                            label: 'Valid Structure',
                            items: [
								'guides/valid-structure/resource-fields',
								'guides/valid-structure/policy-fields',
                            ]
                        },
						'guides/action-authoring/action',
					]
				},
				{
					type: 'category',
					label: 'Vendor Setup',
					items: [
						'guides/vendor-setup/aws-config/setup',
						'guides/vendor-setup/guardduty/setup',
						// 'guides/vendor-setup/prisma-cloud/setup',
						'guides/vendor-setup/adding-vendors'
					]
				},

				{
					type: 'category',
					label: 'Operations',
					items: ['guides/ops/updating-dassana']
				}
			]
		},
		'support'
	]
}
